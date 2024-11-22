package com.example.budd_server.service;

import com.example.budd_server.entity.Response;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.ResponseRepository;
import com.example.budd_server.repository.UserRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CallService {

    private final Dotenv dotenv = Dotenv.load();

    private final String ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
    private final String AUTH_TOKEN = dotenv.get("TWILIO_AUTH_TOKEN");
    private final String FROM_NUMBER = dotenv.get("TWILIO_PHONE_NUMBER");
    private final String ACTION_URL = dotenv.get("TWILIO_ACTION_URL"); // ngrok 설정
    private final String NGROK_URL = dotenv.get("ngrok.baseURL");

    String mp3Url = NGROK_URL + "meal.mp3";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private QuestionService questionService;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public Call makeCall(String to) {
        // 전화번호로 사용자 찾기
        String phoneNumber = "+" + to.replaceAll("\\s+", "");
        System.out.println("입력된 전화번호: " + phoneNumber);
        User user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        int userId = user.getUserId();
        LocalDate date = LocalDate.now(); // 현재 날짜를 가져옵니다.

        // 오늘 날짜에 대한 Response가 이미 있는지 확인
        if (!responseRepository.existsByUserIdAndDate(userId, date)) {
            // 오늘 날짜에 대한 Response가 없으면 새로운 Response 항목 생성
            Response response = new Response();
            response.setUserId(userId);
            response.setDate(date);
            responseRepository.save(response); // 새로운 응답 문서 저장
            System.out.println("새로운 Response 항목 생성: " + response);
        } else {
            System.out.println("오늘 날짜에 대한 응답이 이미 존재합니다.");
        }

        try {
            String firstQuestion = questionService.askFirstQuestion(); // 첫 질문 가져오기
            String twimlResponse = buildGatherTwiml(firstQuestion);

            return Call.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(FROM_NUMBER),
                    new Twiml(twimlResponse)
            ).setRecord(true).create();
        } catch (Exception e) {
            System.err.println("Error making call: " + e.getMessage());
            throw e;
        }
    }

    // Gather를 구성하는 메서드
    private String buildGatherTwiml(String question) {
        return "<Response>" +
                "<Gather input='speech' action='"+ ACTION_URL + "' method='POST' timeout='30' speechTimeout='auto' language='ko-KR'>" +
                "<Play>" + NGROK_URL + question + "</Play>" +  // 음성 파일 재생 +
//                "<Say>" + question + "</Say>" +
                "</Gather>" +
                "</Response>";
    }
}
