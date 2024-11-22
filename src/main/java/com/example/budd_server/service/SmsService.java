package com.example.budd_server.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private final Dotenv dotenv = Dotenv.load();

    private final String ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
    private final String AUTH_TOKEN = dotenv.get("TWILIO_AUTH_TOKEN");
    private final String FROM_NUMBER = dotenv.get("TWILIO_PHONE_NUMBER");  // Twilio 발신 번호

    @PostConstruct
    public void initTwilio() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    // sms 전송
    public void sendSms(String to, String message) {
        try {
            Message.creator(
                    new PhoneNumber(to), // 수신자 번호
                    new PhoneNumber(FROM_NUMBER), // 발신자 번호
                    message // 메시지 내용
            ).create();
        } catch (Exception e) {
            System.err.println("Error sending SMS: " + e.getMessage());
            throw e;
        }
    }
}