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
    private final String FROM_NUMBER = dotenv.get("TWILIO_PHONE_NUMBER");  // Twilio 발신 번호

    @Autowired
    private QuestionService questionService;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public Call makeCall(String to) {
        try {
            String firstQuestion = questionService.askFirstQuestion(); // 첫 질문 가져오기
            String twimlResponse = "<Response>" +
                    "<Gather input='speech' action='/twilio/handle-recording' method='POST' timeout='5' speechTimeout='auto'>" +
                    "<Say>" + firstQuestion + "</Say>" +
                    "</Gather>" +
                    "</Response>";

            Twiml twiml = new Twiml(twimlResponse);

            return Call.creator(
                            new PhoneNumber(to),
                            new PhoneNumber(FROM_NUMBER),
                            twiml
                    )
                    .setRecord(true)  // 녹음 기능 활성화
                    .create();
        } catch (Exception e) {
            System.err.println("Error making call: " + e.getMessage());
            throw e;
        }
    }


}
