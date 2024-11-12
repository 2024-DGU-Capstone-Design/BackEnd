package com.example.budd_server.service;

import com.example.budd_server.dto.ChatGPTDto;
import com.example.budd_server.entity.Response;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.ResponseRepository;
import com.example.budd_server.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class QuestionService {
    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatGPTService chatGPTService;

    @Autowired
    private TTSService ttsService;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean responseReceived = false;
    private String currentQuestion = "";
    private final Dotenv dotenv = Dotenv.load();
    private final String NGROK_URL = dotenv.get("ngrok.baseURL");

    private final String firstQuestion = "meal.mp3";
    private final String healthQuestion = "health.mp3";
    private final String medicineQuestion = "medicine.mp3";
    private final String lastQuestion = "last.mp3";

    // 첫 질문을 요청하는 메서드
    public String askFirstQuestion() {
        responseReceived = false;
        currentQuestion = firstQuestion;
        startNoResponseCheck();  // 응답 대기 타이머 시작
        return currentQuestion;
    }

    public String handleResponse(String response, String phoneNumber) {
        responseReceived = true;  // 응답이 도착했음을 표시

        User user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            return "사용자를 찾을 수 없습니다.";
        }
        int userId = user.getUserId();

        if (currentQuestion == null || currentQuestion.isEmpty()) {
            currentQuestion = firstQuestion;
        }

        // lastQuestion일 때는 응답 판별 없이 그대로 저장하고 종료 처리
        if (currentQuestion.equals(lastQuestion)) {
            String lastResponseUrl = handleMoodResponse(Optional.empty(), response, userId);
            return "<Response><Play>" + lastResponseUrl + "</Play><Hangup/></Response>";
        }

        // 예외 상황 처리
        if (response == null || response.isEmpty()) {
            return askAgainOrEndCall();  // 발화가 없는 경우
        }

        String cleanedResponse = response.replace(".", "").replace(",", "").trim();
        Optional<Boolean> commonResponse = handleCommonResponse(cleanedResponse);

        // 발화가 있지만 질문과 관련 없는 경우
        if (commonResponse.isEmpty() && !isRepeatRequest(cleanedResponse)) {
            return askAgain();  // 재질문
        }

        // "다시 말해줘", "뭐라고?" 같은 응답의 경우
        if (isRepeatRequest(cleanedResponse)) {
            return askAgain();  // 재질문
        }

        // 현재 질문에 따른 응답 처리
        switch (currentQuestion) {
            case firstQuestion:
                handleMealResponse(commonResponse, userId);
                return handleFirstQuestion(commonResponse);
            case healthQuestion:
                handleHealthResponse(commonResponse, userId);
                return handleHealthQuestion(commonResponse);
            case medicineQuestion:
                handleMedicineResponse(commonResponse, userId);
                return handleMedicineQuestion(commonResponse);
            case lastQuestion:
                String lastResponseUrl = handleMoodResponse(commonResponse, response, userId);
                return "<Response><Play>" + lastResponseUrl + "</Play><Hangup/></Response>";
            default:
                return "죄송해요, 다시 한 번 말씀해 주세요.";
        }
    }

    private void startNoResponseCheck() {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                if (!responseReceived) {
                    askAgainOrEndCall();
                }
            }
        }, 15, TimeUnit.SECONDS);
    }

    private String askAgainOrEndCall() {
        if (!responseReceived) {
            responseReceived = false;
            System.out.println("응답이 없어 재질문 또는 종료합니다.");

            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    System.out.println("15초 경과, 응답이 없는 경우를 확인 중입니다.");  // 예약된 작업 진입 로그
                    if (!responseReceived) {
                        System.out.println("답변이 없어 통화를 종료합니다.");  // 종료 로그
                        // 실제 통화 종료 로직을 여기서 처리하도록 설정합니다.
                    }
                }
            }, 15, TimeUnit.SECONDS);

            return currentQuestion;
        }
        return null;
    }


    private boolean isRepeatRequest(String response) {
        return response.contains("뭐라고") || response.contains("다시 말해줘");
    }

    private String askAgain() {
        System.out.println("질문과 관련 없는 응답이므로 재질문을 요청합니다.");
        return currentQuestion;
    }

    private String handleFirstQuestion(Optional<Boolean> response) {
        if (response.isPresent()) {
            currentQuestion = healthQuestion;
            return healthQuestion;
        }
        return askAgain();
    }

    private String handleHealthQuestion(Optional<Boolean> response) {
        if (response.isPresent()) {
            currentQuestion = medicineQuestion;
            return medicineQuestion;
        }
        currentQuestion = lastQuestion;
        return lastQuestion;
    }

    private String handleMedicineQuestion(Optional<Boolean> response) {
        if (response.isPresent()) {
            currentQuestion = lastQuestion;
            return lastQuestion;
        }
        currentQuestion = lastQuestion;
        return lastQuestion;
    }

    private void handleMealResponse(Optional<Boolean> response, int userId) {
        handleResponseStorage(response, userId, "meal");
    }

    private void handleHealthResponse(Optional<Boolean> response, int userId) {
        handleResponseStorage(response, userId, "disease");
    }

    private void handleMedicineResponse(Optional<Boolean> response, int userId) {
        handleResponseStorage(response, userId, "medicine");
    }

    private String handleMoodResponse(Optional<Boolean> response, String originalResponse, int userId) {
        handleResponseStorage(response, userId, "mood");

        ChatGPTDto chatGPTDto = new ChatGPTDto();
        chatGPTDto.setResponsePrompt(originalResponse);

        Map<String, Object> chatGPTResponse = chatGPTService.responsePrompt(chatGPTDto);

        String responseText = "";
        if (chatGPTResponse.containsKey("choices")) {
            responseText = ((Map<String, Object>) ((List<Object>) chatGPTResponse.get("choices")).get(0)).get("text").toString();
        }

        String ttsFileName = ttsService.convertTextToSpeech(responseText, "lastQuestion.mp3");

        return ttsFileName;
    }

    private void handleResponseStorage(Optional<Boolean> response, int userId, String type) {
        if (response.isPresent()) {
            Response dbResponse = responseRepository.findByUserIdAndDate(userId, LocalDate.now());
            if (dbResponse != null) {
                switch (type) {
                    case "meal":
                        dbResponse.setMeal(response.get());
                        break;
                    case "disease":
                        dbResponse.setDisease(response.get());
                        break;
                    case "medicine":
                        dbResponse.setMedicine(response.get());
                        break;
                    case "mood":
                        dbResponse.setMood(response.get());
                        break;
                }
                responseRepository.save(dbResponse);
            }
        }
    }

    private Optional<Boolean> handleCommonResponse(String response) {
        if (response.contains("응")) {
            return Optional.of(true);
        } else if (response.contains("아니")) {
            return Optional.of(false);
        }
        return Optional.empty();
    }
}
