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
import org.springframework.web.client.RestTemplate;
import java.io.*;

import java.net.URI;
import java.util.*;

@Service
public class TwilioService {
    Dotenv dotenv = Dotenv.load();

    String accountSid = dotenv.get("twilio.accountSid");

    String authToken = dotenv.get("twilio.authToken");

    String fromPhoneNumber = dotenv.get("twilio.phoneNumber");

//    String googleBucketURL = dotenv.get("google.bucketURL");

    String ngrokBaseURL = dotenv.get("ngrok.baseURL");

    @Autowired
    private GoogleTtsService googleTtsService;

    // Twilio 초기화
    public TwilioService() {
        Twilio.init(accountSid, authToken);  // Twilio 계정 SID와 Auth Token으로 초기화
    }

    public String startCallFlow(String toPhoneNumber) {
        String ttsFilePath = googleTtsService.generateTTS("안녕하세요, 오늘 하루 어떠셨나요?");
        // Google Cloud Storage의 MP3 파일 URL
//        String mp3Url = googleBucketURL + "output.wav";
        String mp3Url = ngrokBaseURL + "tts_output.mp3";

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

    public String startCallWithRecording(String toPhoneNumber) {
        // Twilio로 전화 걸기 설정 (recordingStatusCallback을 설정하여 녹음 파일 URL 받기)
        Call call = Call.creator(
                        new PhoneNumber(toPhoneNumber),        // 대상 번호
                        new PhoneNumber(fromPhoneNumber),      // Twilio 전화번호
                        URI.create(ngrokBaseURL+"twilio-callback")  // Twilio 콜백 URL 설정 (음성 파일 처리)
                )
                .setRecord(true)  // 녹음 활성화
                .setRecordingStatusCallback(String.valueOf(URI.create(ngrokBaseURL + "stt-callback")))  // 녹음이 끝나면 이 URL로 녹음 파일 URL을 콜백
                .create();

        System.out.println("Call initiated with recording enabled.");
        return "Call Started";
    }

    private void deleteFileAfterCall(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();  // 파일 삭제
        }
    }

    //twilio에서 받은 녹음 파일 URL을 STT로 처리하는 메소드
//    public String processSttResponse(String recordingUrl) {
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//
//            // 녹음 파일을 Google STT로 보낼 준비
//            byte[] audioBytes = restTemplate.getForObject(recordingUrl, byte[].class);
//            String encodedAudio = Base64.getEncoder().encodeToString(audioBytes);
//
//            Map<String, Object> body = new HashMap<>();
//            body.put("audio", Map.of("content", encodedAudio));
//            body.put("config", Map.of("languageCode", "ko-KR"));
//
//            // Google STT API 호출
//            String googleSttUrl = "https://speech.googleapis.com/v1/speech:recognize?key=" + dotenv.get("google.apiKey");
//            Map<String, Object> response = restTemplate.postForObject(googleSttUrl, body, Map.class);
//
//            // 음성 텍스트 추출
//            String transcript = (String) ((Map<String, Object>) ((List<Map<String, Object>>) response.get("results")).get(0).get("alternatives")).get(0).get("transcript");
//
//            // 콘솔에 텍스트 출력
//            System.out.println("Recognized Text: " + transcript);
//
//            return transcript;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error processing STT";
//        }
//    }

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
