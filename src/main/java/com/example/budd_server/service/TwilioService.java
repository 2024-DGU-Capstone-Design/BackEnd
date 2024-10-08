package com.example.budd_server.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Play;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class TwilioService {
    Dotenv dotenv = Dotenv.load();

    String accountSid = dotenv.get("twilio.accountSid");

    String authToken = dotenv.get("twilio.authToken");

    String fromPhoneNumber = dotenv.get("twilio.phoneNumber");

    String googleBucketURL = dotenv.get("google.bucketURL");

//    @Autowired
//    private GoogleTtsService googleTtsService;

    // Twilio 초기화
    public TwilioService() {
        Twilio.init(accountSid, authToken);  // Twilio 계정 SID와 Auth Token으로 초기화
    }

    public String startCallFlow(String toPhoneNumber) {
        // Google Cloud Storage의 MP3 파일 URL
        String mp3Url = googleBucketURL + "first.mp3";

        // Twiml 생성
        Play play = new Play.Builder(mp3Url).build();
        VoiceResponse response = new VoiceResponse.Builder().play(play).build();
        Twiml twiml = new Twiml(response.toXml());

        // Twilio로 전화 걸기
        initiateCall(toPhoneNumber, twiml);

        return "Call Started";
    }


    // Twilio로 전화 걸기
    private void initiateCall(String toPhoneNumber, Twiml twiml) {
        Call.creator(
                new PhoneNumber(toPhoneNumber),        // 수신자 번호
                new PhoneNumber(fromPhoneNumber),      // 발신자 번호
                twiml
        ).create();
    }

    // STT 결과 처리 (STT는 이후 구현할 부분)
    private String waitForResponse(int seconds, String audioFilePath) {
        // STT API 호출로 응답 처리
        // return getSTTResult(audioFilePath);
        return null;
    }

    // GPT 응답 처리
//    private void processGptResponse(String userResponse) {
//        String gptReply = "그렇군요";  // 기본 응답
//        initiateCall("YOUR_TWILIO_PHONE_NUMBER", googleTtsService.getGoogleTTS(gptReply));
//    }
}
