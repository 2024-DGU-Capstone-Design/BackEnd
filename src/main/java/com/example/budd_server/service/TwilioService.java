package com.example.budd_server.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class TwilioService {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String fromPhoneNumber;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    public String makeCall(String toPhoneNumber) {
        try {
            Call call = Call.creator(
                    new PhoneNumber(toPhoneNumber),   // 받는 사람 번호
                    new PhoneNumber(fromPhoneNumber), // Twilio 인증 번호
                    URI.create("http://demo.twilio.com/docs/voice.xml")
            ).create();

            return call.getSid();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to make call: " + e.getMessage();
        }
    }
}
