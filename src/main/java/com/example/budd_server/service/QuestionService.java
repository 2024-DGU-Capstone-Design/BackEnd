package com.example.budd_server.service;

import com.example.budd_server.entity.Response;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.ResponseRepository;
import com.example.budd_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private GoogleTTSService googleTTSService;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean responseReceived = false;
    private String currentQuestion = "";

    // 질문 변수
    private final String firstQuestion = "meal.mp3";
    private final String healthQuestion = "health.mp3";
    private final String medicineQuestion = "medicine.mp3";
    private final String lastQuestion = "last.mp3";

    //응답 변수
    private final String mealAnswer1  = "meal_answer1.mp3";
    private final String mealAnswer2  = "meal_answer2.mp3";
    private final String pardon  = "pardon.mp3";
    private final String conclusion  = "conclusion.mp3";

    private final String gpt  = "gpt.mp3";

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
                handleMoodResponse(commonResponse, userId);  // 응답을 저장

                // 사용자의 원본 응답을 그대로 전달
                return handleLastQuestion(response);

            default:
                return pardon;
        }
    }
    private String handleLastQuestion(String userResponse) {
        if (userResponse != null && !userResponse.isEmpty()) {
            // 사용자의 응답을 TTS로 변환하여 파일 생성
            System.out.println("사용자 응답: " + userResponse);
            String filePath = googleTTSService.generateTTS(userResponse, "gpt.mp3");

//            // 비동기로 파일 삭제 스케줄링 (1분 후)
//            scheduler.schedule(() -> googleTTSService.deleteFile(filePath), 1, TimeUnit.MINUTES);

            // 생성된 파일 경로 반환 (Twilio가 재생)
            return filePath;
        }
        return conclusion;
    }



    private String handleFirstQuestion(Optional<Boolean> response) {
        if (response.isPresent()) {
            currentQuestion = healthQuestion;
            return healthQuestion;
        }
        return pardon;
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

    private void handleMoodResponse(Optional<Boolean> response, int userId) {
        handleResponseStorage(response, userId, "mood");
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
