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
    private ChatGPTService chatGPTService;  // ChatGPTService 주입

    @Autowired
    private TTSService ttsService;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean responseReceived = false;
    private String currentQuestion = "";
    private final Dotenv dotenv = Dotenv.load();
    private final String NGROK_URL = dotenv.get("ngrok.baseURL");

    // 질문 변수
    private final String firstQuestion = "meal.mp3";
    private final String healthQuestion = "health.mp3";
    private final String medicineQuestion = "medicine.mp3";
    private final String lastQuestion = "last.mp3";

    // 첫 질문을 요청하는 메서드
    public String askFirstQuestion() {
        responseReceived = false;
        currentQuestion = firstQuestion;  // 첫 질문 설정
        System.out.println("첫 질문 설정: " + currentQuestion);
        return currentQuestion;  // 첫 질문 반환
    }

    // 사용자의 응답을 처리하는 메서드
    public String handleResponse(String response, String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber); // 전화번호로 사용자 찾기
        if (user == null) {
            return "사용자를 찾을 수 없습니다.";
        }
        int userId = user.getUserId(); // userId 가져오기

        if (currentQuestion == null || currentQuestion.isEmpty()) {
            System.out.println("currentQuestion이 설정되지 않았습니다. 기본 질문을 사용합니다.");
            currentQuestion = firstQuestion;  // 기본 질문 설정
        }

        responseReceived = true;

        // 현재 질문과 응답을 로그로 출력
        System.out.println("현재 질문: " + currentQuestion);
        System.out.println("사용자 응답: " + response);

        // 응답의 마침표와 쉼표를 제거하고 비교
        String cleanedResponse = response.replace(".", "").replace(",", "").trim();
        Optional<Boolean> commonResponse = handleCommonResponse(cleanedResponse);

        // 현재 질문에 따라 응답을 처리
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

    private String handleFirstQuestion(Optional<Boolean> response) {
        if (response.isPresent()) {
            currentQuestion = healthQuestion;
            return healthQuestion;
        }
        return "죄송해요, 다시 한 번 말씀해 주세요.";
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

        // ChatGPT 응답 요청 생성
        ChatGPTDto chatGPTDto = new ChatGPTDto();
        chatGPTDto.setResponsePrompt(originalResponse);

        // ChatGPT API 호출 및 응답 가져오기
        Map<String, Object> chatGPTResponse = chatGPTService.responsePrompt(chatGPTDto);

        // ChatGPT 응답 텍스트 추출
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
                System.out.println(type + "에 대한 응답 저장: " + response.get());
                System.out.println("최종 질문 파일 설정: " + currentQuestion);

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
