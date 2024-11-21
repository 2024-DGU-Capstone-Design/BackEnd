package com.example.budd_server.service;

import com.example.budd_server.dto.ChatGPTDto;
import com.example.budd_server.entity.Response;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.ResponseRepository;
import com.example.budd_server.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
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

    private final String mealAnswer1 = "meal_answer1.mp3"; //좋아요, 앞으로도 잘 챙겨드세요. 건강은 어떠세요?
    private final String mealAnswer2 = "meal_answer2.mp3"; //식사는 잘 챙겨 드셔야 해요. 건강은 어떠세요?
    private final String medicineAnswer1 = "medicine_answer1.mp3"; //좋아요. 오늘 하루는 어떠셨나요?
    private final String medicineAnswer2 = "medicine_answer2.mp3"; //약 잘 챙겨 드셔야 해요. 오늘 하루는 어떠셨나요?

    private final String pardon = "pardon.mp3"; //죄송해요. 잘 들리지 않았어요. 다시 한 번 말씀해주시겠어요?

    private static final int POST_CREATION_WAIT = 5000; // 파일 생성 후 추가 대기 시간 (밀리초)



    // 첫 질문을 요청하는 메서드
    public String askFirstQuestion() {
        responseReceived = false;
        currentQuestion = firstQuestion;
        startNoResponseCheck();  // 응답 대기 타이머 시작
        return currentQuestion;
    }

    //질문별 응답 처리 로직
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
            return "<Response><Play>files/" + lastResponseUrl + "</Play><Hangup/></Response>";
        }

        // 예외 상황 처리
        if (response == null || response.isEmpty()) {
            return askAgainOrEndCall();  // 발화가 없는 경우
        }

        String cleanedResponse = response.replace(".", "").replace(",", "").trim();
        Optional<Boolean> commonResponse = handleCommonResponse(cleanedResponse);

//        // 발화가 있지만 질문과 관련 없는 경우
//        if (commonResponse.isEmpty() && !isRepeatRequest(cleanedResponse)) {
//            return askAgain();  // 재질문
//        }
//
//        // "다시 말해줘", "뭐라고?" 같은 응답의 경우
//        if (isRepeatRequest(cleanedResponse)) {
//            return askAgain();  // 재질문
//        }

        // 현재 질문에 따른 응답 처리
        switch (currentQuestion) {
            case firstQuestion:
                // meal 관련 응답 처리
                Optional<Boolean> mealResponse = handleMealResponse(cleanedResponse);
                if (mealResponse.isEmpty()) {
                    return askAgain();  // 정의되지 않은 응답일 경우 재질문
                }
                handleMealResponse(mealResponse, userId); // 응답 저장
                return handleFirstQuestion(mealResponse); // 다음 단계로 진행
            case mealAnswer1:
            case mealAnswer2:
            case healthQuestion:
                Optional<Boolean> healthResponse = handleHealthResponse(cleanedResponse);
                if (healthResponse.isEmpty()) {
                    return askAgain();  // 정의되지 않은 응답일 경우 재질문
                }
                handleHealthResponse(healthResponse, userId);
                return handleHealthQuestion(healthResponse);
            case medicineQuestion:
                Optional<Boolean> medicineResponse = handleMealResponse(cleanedResponse);
                if (medicineResponse.isEmpty()) {
                    return askAgain();  // 정의되지 않은 응답일 경우 재질문
                }
                handleMedicineResponse(medicineResponse, userId);
                return handleMedicineQuestion(medicineResponse);
            case medicineAnswer1:
            case medicineAnswer2:
            case lastQuestion:
                String lastResponseUrl = handleMoodResponse(Optional.empty(), response, userId);
                return "<Response><Play>files/" + lastResponseUrl + "</Play><Hangup/></Response>";
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

    //잘 지내셨나요? 밥은 드셨나요?
    private String handleFirstQuestion(Optional<Boolean> response) {
        if (response.isPresent()) {
            if (response.get()) {
                // "응"인 경우, mealAnswer1을 재생하고 다음 질문으로 넘어감
                currentQuestion = healthQuestion;
                return mealAnswer1;
            } else {
                // "아니"인 경우, mealAnswer2을 재생하고 다음 질문으로 넘어감
                currentQuestion = healthQuestion;
                return mealAnswer2;
            }
        }
        return askAgain();  // 명확한 응답이 없는 경우, 재질문
    }


    //건강은 어떠세요?
    private String handleHealthQuestion(Optional<Boolean> response) {
        if (response.isPresent()) {
            if (response.get()) {
                // "응"인 경우, medicine을 재생하고 다음 질문으로 넘어감
                currentQuestion = medicineQuestion;
                return medicineQuestion;
            } else {
                // "아니"인 경우, medicineAnswer1을 재생하고 다음 질문으로 넘어감
                currentQuestion = lastQuestion;
                return medicineAnswer1;
            }
        }
        return askAgain();  // 명확한 응답이 없는 경우, 재질문
    }

    private String handleMedicineQuestion(Optional<Boolean> response) {
        if (response.isPresent()) {
            if (response.get()) {
                // "응"인 경우, medicineAnswer1을 재생하고 다음 질문으로 넘어감
                currentQuestion = lastQuestion;
                return medicineAnswer1;
            } else {
                // "아니"인 경우, medicineAnswer2을 재생하고 다음 질문으로 넘어감
                currentQuestion = lastQuestion;
                return medicineAnswer2;
            }
        }
        return askAgain();  // 명확한 응답이 없는 경우, 재질문
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

        // ChatGPT를 통해 응답 생성
        ChatGPTDto chatGPTDto = new ChatGPTDto();
        chatGPTDto.setResponsePrompt(originalResponse);
        Map<String, Object> chatGPTResponse = chatGPTService.responsePrompt(chatGPTDto);

        String responseText = "";
        if (chatGPTResponse.containsKey("choices")) {
            responseText = ((Map<String, Object>) ((List<Object>) chatGPTResponse.get("choices")).get(0)).get("text").toString();
        }

        // TTS 파일 생성 (userId를 파일명으로 사용)
        String ttsFilePath = String.valueOf(userId);
        ttsService.convertTextToSpeech(responseText, ttsFilePath);

        waitForPostCreationDelay();
        return userId + ".mp3"; // 파일 경로 반환
    }

    private void waitForPostCreationDelay() {
        try {
            Thread.sleep(POST_CREATION_WAIT); // 추가 대기 시간 (2초)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("추가 대기 중 인터럽트 발생", e);
        }
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

    //식사 및 약 복용 여부 질문에 대한 응답 처리
    private Optional<Boolean> handleMealResponse(String response) {
        if (response == null || response.isBlank()) {
            return Optional.empty();
        }

        // 텍스트 정규화: 특수기호 및 공백 제거
        String normalizedResponse = response
                .replaceAll("[^a-zA-Z가-힣0-9 ]", "")
                .trim()
                .toLowerCase();

        // 긍정 답변들
        String[] positiveResponses = {
                "먹었어", "먹었어요", "먹을 거야", "어", "챙겨 먹었어"
        };

        // 부정 답변들
        String[] negativeResponses = {
                "아직", "안 먹었어", "안 먹었어요", "안 먹을 거야"
        };

        // 일치하는 긍정/부정 답변 확인
        for (String positive : positiveResponses) {
            if (normalizedResponse.equals(positive)) {
                return Optional.of(true);
            }
        }

        for (String negative : negativeResponses) {
            if (normalizedResponse.equals(negative)) {
                return Optional.of(false);
            }
        }

        if (normalizedResponse.contains("응")) {
            return Optional.of(true);
        }

        if (normalizedResponse.contains("아니")) {
            return Optional.of(false);
        }

        // 추가 처리: 긍/부정을 유추할 수 있는 일반적인 표현 분석
        if (normalizedResponse.contains("먹었")) {
            return Optional.of(true);
        } else if (normalizedResponse.contains("안 먹") || normalizedResponse.contains("못 먹")) {
            return Optional.of(false);
        }

        // 예상치 못한 경우
        return Optional.empty();
    }

    // "건강은 어떠세요?" 질문에 대한 응답 처리
    private Optional<Boolean> handleHealthResponse(String response) {
        if (response == null || response.isBlank()) {
            return Optional.empty();
        }

        // 텍스트 정규화: 특수기호 및 공백 제거
        String normalizedResponse = response
                .replaceAll("[^a-zA-Z가-힣0-9 ]", "")
                .trim()
                .toLowerCase();

        // 긍정 답변들 (아픈 상태)
        String[] positiveResponses = {
                "아파", "아파요", "문제 있어", "있어", "어", "아니야"
        };

        // 부정 답변들 (건강한 상태)
        String[] negativeResponses = {
                "괜찮아", "괜찮아요", "문제 없어", "건강해요", "건강해"
        };

        // 정확히 일치하는 긍정/부정 답변 확인
        for (String positive : positiveResponses) {
            if (normalizedResponse.equals(positive)) {
                return Optional.of(true);
            }
        }

        for (String negative : negativeResponses) {
            if (normalizedResponse.equals(negative)) {
                return Optional.of(false);
            }
        }

        // '응', '네' 포함 시 긍정 처리
        if (normalizedResponse.contains("응") && !normalizedResponse.contains("네")) {
            return Optional.of(true);
        }

        // '아니', '없어' 포함 시 부정 처리
        if (normalizedResponse.contains("아니") || normalizedResponse.contains("없어")) {
            return Optional.of(false);
        }

        // 예상치 못한 경우
        return Optional.empty();
    }
}
