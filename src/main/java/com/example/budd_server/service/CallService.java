package com.example.budd_server.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class CallService {

    private final Dotenv dotenv = Dotenv.load();

    private final String ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
    private final String AUTH_TOKEN = dotenv.get("TWILIO_AUTH_TOKEN");
    private final String FROM_NUMBER = dotenv.get("TWILIO_PHONE_NUMBER");  // Twilio 발신 번호

    @PostConstruct
    public void initTwilio() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public Call makeCall(String to) {
        try {
            Twiml twiml = new Twiml("<Response><Say>안녕하세요, 테스트 전화입니다.</Say></Response>");

            return Call.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(FROM_NUMBER),
                    twiml
            ).create();
        } catch (Exception e) {
            System.err.println("Error making call: " + e.getMessage());
            throw e;
        }
    }
}
