package com.example.budd_server.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class QuestionService {

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean responseReceived = false;
    private String currentQuestion = "";

    // 질문 변수
    private String firstQuestion = "할머니 잘 지내셨어요? 밥 잘 먹었어요?";
    private String healthQuestion = "아픈 곳은 있어요?";
    private String medicineQuestion = "약은 먹었어요?";
    private String lastQuestion = "오늘 별일 없었어요?";

    // 첫 질문을 요청하는 메서드
    public String askFirstQuestion() {
        responseReceived = false;
        currentQuestion = firstQuestion;  // 첫 질문 설정
        System.out.println("첫 질문 설정: " + currentQuestion);
        return currentQuestion;  // 첫 질문 반환
    }

    // 사용자의 응답을 처리하는 메서드
    public String handleResponse(String response) {
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

        // 현재 질문에 따라 응답을 처리
        switch (currentQuestion) {
            case "할머니 잘 지내셨어요? 밥 잘 먹었어요?":
                return handleFirstQuestion(cleanedResponse);
            case "아픈 곳은 있어요?":
                return handleHealthQuestion(cleanedResponse);
            case "약은 먹었어요?":
                return handleMedicineQuestion(cleanedResponse);
            case "오늘 별일 없었어요?":
                return "오늘도 건강하게 지내세요!";
            default:
                return "죄송해요, 다시 한 번 말씀해 주세요.";
        }
    }

    private String handleFirstQuestion(String response) {
        if (response.equalsIgnoreCase("응 먹었어요")) {
            currentQuestion = healthQuestion;  // 다음 질문 설정
            System.out.println("응답 처리 후 다음 질문: " + currentQuestion);
            return "아픈 곳은 있어요?";  // 다음 질문 사용자에게 전달
        } else if (response.equalsIgnoreCase("아니 안 먹었어요")) {
            currentQuestion = healthQuestion;  // 다음 질문 설정
            System.out.println("응답이 '아니'일 때 다음 질문: " + currentQuestion);
            return "밥 잘 챙겨 먹어요~ 아픈 곳은 있어요?";
        } else {
            return "죄송해요, 다시 한 번 말씀해 주세요.";
        }
    }

    private String handleHealthQuestion(String response) {
        if (response.equalsIgnoreCase("응 아파요")) {
            currentQuestion = medicineQuestion;
            return currentQuestion;  // 다음 질문 반환
        } else {
            currentQuestion = lastQuestion;
            return "오늘 별일 없었어요?";  // 다음 질문 반환
        }
    }

    private String handleMedicineQuestion(String response) {
        if (response.equalsIgnoreCase("응 먹었어요")) {
            currentQuestion = lastQuestion;
            return currentQuestion;  // 다음 질문 반환
        } else {
            currentQuestion = lastQuestion;
            return "오늘 별일 없었어요?";  // 마지막 질문 반환
        }
    }

    private void startResponseTimer() {
        scheduler.schedule(() -> {
            if (!responseReceived) {
                System.out.println("응답이 없습니다. 질문을 반복합니다.");
            }
        }, 30, TimeUnit.SECONDS);  // 응답 대기 30초
    }
}
