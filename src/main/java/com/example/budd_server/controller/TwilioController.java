package com.example.budd_server.controller;

import com.example.budd_server.service.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173") // CORS 문제 해결
@RestController
@RequestMapping("/api")
public class TwilioController {

    @Autowired
    private TwilioService twilioService;

    // 전화 걸기 API
    @PostMapping("/call")
    public String makeCall(@RequestParam("to") String to) {
        return twilioService.startCall(to);
    }

    // Twilio에서 실시간 음성 스트리밍을 WebSocket으로 처리
    @PostMapping("/media-stream")
    public void handleMediaStream(@RequestBody String streamData) {
        System.out.println("Received stream data: " + streamData);
    }

    @GetMapping("/test")
    public String testWebSocket() {
        return "WebSocket 서버는 /media-stream 경로에서 동작 중입니다. WebSocket 클라이언트로 테스트하세요.";
    }
}
