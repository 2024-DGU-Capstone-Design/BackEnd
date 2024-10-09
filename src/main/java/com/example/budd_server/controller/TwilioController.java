package com.example.budd_server.controller;

import com.example.budd_server.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwilioController {

    @Autowired
    private QuestionService questionService;

    @PostMapping("/twilio/handle-recording")
    public String handleRecording(
            @RequestParam(value = "SpeechResult", required = false) String speechResult,
            @RequestParam(value = "Confidence", required = false) String confidence,
            @RequestParam(value = "CallSid", required = false) String callSid,
            @RequestParam(value = "From", required = false) String from,
            @RequestParam(value = "To", required = false) String to
    ) {
        try {
            System.out.println("Received SpeechResult: " + speechResult);
            System.out.println("Confidence: " + confidence);
            System.out.println("CallSid: " + callSid);
            System.out.println("From: " + from);
            System.out.println("To: " + to);

            if (speechResult != null && !speechResult.isEmpty()) {
                // QuestionService의 handleResponse 메서드 호출
                String response = questionService.handleResponse(speechResult);
                return "응답이 처리되었습니다: " + response;  // 처리된 응답 반환
            } else {
                return "응답이 없습니다.";
            }
        } catch (Exception e) {
            System.err.println("Error processing recording: " + e.getMessage());
            return "처리 중 오류가 발생했습니다.";
        }
    }
}
