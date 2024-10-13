package com.example.budd_server.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallService {

    private final Dotenv dotenv = Dotenv.load();

    private final String ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
    private final String AUTH_TOKEN = dotenv.get("TWILIO_AUTH_TOKEN");
    private final String FROM_NUMBER = dotenv.get("TWILIO_PHONE_NUMBER");
    private final String ACTION_URL = dotenv.get("TWILIO_ACTION_URL"); // ngrok 설정

    @Autowired
    private QuestionService questionService;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public Call makeCall(String to) {
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
                "<Say>" + question + "</Say>" +
                "</Gather>" +
                "</Response>";
    }

    // 다음 질문 요청 메서드
    public void askNextQuestion(String to, String question) {
        String twimlResponse = buildGatherTwiml(question);
        Call.creator(
                new PhoneNumber(to),
                new PhoneNumber(FROM_NUMBER),
                new Twiml(twimlResponse)
        ).setRecord(true).create();
    }
}
