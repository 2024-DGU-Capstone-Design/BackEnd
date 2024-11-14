package com.example.budd_server.controller;

import com.example.budd_server.service.QuestionService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwilioController {

    private final Dotenv dotenv = Dotenv.load();
    private final String ACTION_URL = dotenv.get("TWILIO_ACTION_URL");
    private final String NGROK_URL = dotenv.get("ngrok.baseURL");

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
                // QuestionService의 handleResponse 호출
                String response = questionService.handleResponse(speechResult, to);
                return buildGatherTwiml(response);  // 다음 질문을 포함한 TwiML 반환
            } else {
                return buildGatherTwiml("응답이 없습니다.");
            }
        } catch (Exception e) {
            System.err.println("Error processing recording: " + e.getMessage());
            return buildGatherTwiml("처리 중 오류가 발생했습니다.");
        }
    }


    private String buildGatherTwiml(String question) {
        System.out.println("NGROK_URL: " + NGROK_URL);
        System.out.println("Question: " + question);


        String questionUrl = "lastQuestion.mp3".equals(question)
                ? NGROK_URL + "lastQuestion.mp3"  // 마지막 질문
                : NGROK_URL + question;      // 일반 질문 파일

        if ("lastQuestion.mp3".equals(question)) {
            // 마지막 질문 파일을 재생하고 통화 종료
            return "<Response>" +
                    "<Play>" + questionUrl + "</Play>" +
                    "<Hangup/>" +
                    "</Response>";
        } else {
            return "<Response>" +
                    "<Gather input='speech' action='" + NGROK_URL + "twilio/handle-recording' method='POST' timeout='15' speechTimeout='auto' language='ko-KR'>" +
                    "<Play>" + questionUrl + "</Play>" +
                    "</Gather>" +
                    "</Response>";
        }
    }

}
