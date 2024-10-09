package com.example.budd_server.controller;

import com.example.budd_server.service.GoogleSttService;
import com.example.budd_server.service.TwilioService;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Play;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173") //cors 문제 해결
@RestController
@RequestMapping("/api")
public class TwilioController {

    @Autowired
    private TwilioService twilioService;
    @Autowired
    private GoogleSttService googleSttService;

    //전화 걸기
    @PostMapping("/call")
    public String makeCall(@RequestParam("to") String to) {
        return twilioService.startCallFlow(to);
    }

    // Twilio 녹음 콜백 처리 엔드포인트
    @PostMapping("/stt-callback")
    public void handleRecordingCallback(@RequestParam("RecordingUrl") String recordingUrl) {
        System.out.println("Received Recording URL: " + recordingUrl);

        // Google STT로 녹음 파일을 전송하여 텍스트 변환
        String transcript = googleSttService.processStt(recordingUrl);

        // 텍스트가 없을 경우 "응답이 없습니다." 출력
        if (transcript == null || transcript.isEmpty()) {
            System.out.println("응답이 없습니다.");
        } else {
            System.out.println("Recognized Text: " + transcript);
        }
    }
//    @GetMapping("/twilio-callback")
//    public String handleTwiML() {
//        try {
//            // 재생할 MP3 파일 URL
////            String mp3Url = "https://storage.googleapis.com/gglttsbucket/first.mp3";
//            String mp3Url = "https://a165-110-12-129-228.ngrok-free.app/output.mp3";
//
//
//            // TwiML을 사용하여 MP3 파일 재생 및 사용자 응답 녹음 시작
//            Play play = new Play.Builder(mp3Url).build();
//            Gather gather = new Gather.Builder()
//                    .input("speech") // 음성 입력 허용
//                    .timeout(10)     // 10초간 응답 대기
//                    .play(play)      // 음성 파일 재생
//                    .build();
//
//            VoiceResponse response = new VoiceResponse.Builder()
//                    .gather(gather)
//                    .build();
//
//            return response.toXml(); // TwiML XML 응답 반환
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "<Response><Say>오류가 발생했습니다.</Say></Response>";
//        }
//    }
}
