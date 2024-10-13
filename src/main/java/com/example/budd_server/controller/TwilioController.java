package com.example.budd_server.controller;

import com.example.budd_server.service.GoogleTtsService;
import com.example.budd_server.service.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173") // CORS 문제 해결
@RestController
@RequestMapping("/api")
public class TwilioController {

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private GoogleTtsService googleTtsService;

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

    @PostMapping("/generate")
    public ResponseEntity<String> generateTts(@RequestParam("text") String text) {
        try {
            // 서비스에서 음성 파일 생성 로직 호출
            String filePath = googleTtsService.generateTTS(text);

            // 파일 경로를 응답으로 반환
            return ResponseEntity.ok("TTS file generated successfully. Path: " + filePath);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating TTS: " + e.getMessage());
        }
    }
}
