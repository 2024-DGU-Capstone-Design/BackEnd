package com.example.budd_server.controller;

import com.example.budd_server.service.GoogleSTTService;
import com.example.budd_server.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwilioController {

    @Autowired
    private GoogleSTTService googleSTTService;  // Google STT 서비스

    @Autowired
    private QuestionService questionService;  // 질문 서비스

    @PostMapping("/twilio/handle-recording")
    public String handleRecording(@RequestParam("RecordingUrl") String recordingUrl) {
        try {
            // 녹음 파일을 Google STT로 전송하여 텍스트 변환
            String transcribedText = googleSTTService.transcribeAudio(recordingUrl);  // 녹음 파일을 텍스트로 변환

            // 변환된 텍스트 확인 (로그로 출력)
            System.out.println("Transcribed Text: " + transcribedText);

            // 질문 서비스에서 텍스트 기반 응답 처리
            return questionService.handleResponse(transcribedText);
        } catch (Exception e) {
            System.err.println("Error processing recording: " + e.getMessage());
            return "처리 중 오류가 발생했습니다.";
        }
    }
}


