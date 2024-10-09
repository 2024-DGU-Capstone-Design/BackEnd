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

    public String askFirstQuestion() {
        responseReceived = false;
        startResponseTimer();
        currentQuestion = firstQuestion;
        return currentQuestion; // 첫 질문을 반환
    }

    public String handleResponse(String response) {
        // 응답을 받았음을 표시
        responseReceived = true;

        // 현재 질문에 따라 다음 응답을 결정
        switch (currentQuestion) {
            case "밥 잘 먹었어요?":
                return handleFirstQuestion(response);
            case "아픈 곳은 있어요?":
                return handleHealthQuestion(response);
            case "약은 먹었어요?":
                return handleMedicineQuestion(response);
            case "오늘 별일 없었어요?":
                return "오늘도 건강하게 지내세요!";
            default:
                return "죄송해요, 다시 한 번 말씀해 주세요.";
        }
    }

    private String handleFirstQuestion(String response) {
        // 응답에 따라 다음 질문 설정
        if (response.equalsIgnoreCase("네 먹었어요")) {
            currentQuestion = healthQuestion;
            return currentQuestion; // 다음 질문
        } else if (response.equalsIgnoreCase("아니")) {
            currentQuestion = healthQuestion;
            return "밥 잘 챙겨 먹어요~ " + currentQuestion;
        } else {
            return "죄송해요, 다시 한 번 말씀해 주세요.";
        }
    }

    private String handleHealthQuestion(String response) {
        // 건강 관련 질문 처리
        if (response.equalsIgnoreCase("응")) {
            currentQuestion = medicineQuestion;
            return currentQuestion; // 다음 질문
        } else {
            currentQuestion = lastQuestion;
            return "오늘 별일 없었어요?"; // 다음 질문
        }
    }

    private String handleMedicineQuestion(String response) {
        // 약 관련 질문 처리
        if (response.equalsIgnoreCase("응")) {
            currentQuestion = lastQuestion;
            return currentQuestion; // 다음 질문
        } else {
            currentQuestion = lastQuestion;
            return "오늘 별일 없었어요?"; // 다음 질문
        }
    }

    private void startResponseTimer() {
        scheduler.schedule(() -> {
            if (!responseReceived) {
                System.out.println("응답이 없습니다. 질문을 반복합니다.");
                // 질문을 다시 하거나 종료할 수 있습니다.
            }
        }, 30, TimeUnit.SECONDS); // 시간을 30초로 늘림
    }

    private void terminateCall() {
        System.out.println("10초 이상 대답이 없어 전화가 종료됩니다.");
        // 실제 Twilio API를 사용하여 전화 종료 로직 추가
    }
}
