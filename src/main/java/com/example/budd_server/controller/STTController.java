package com.example.budd_server.controller;

import com.example.budd_server.service.GoogleSTTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;

@RestController
public class STTController {

    @Autowired
    private GoogleSTTService googleSTTService;

    @GetMapping("/api/transcribe")
    public String transcribeAudio(@RequestParam("filePath") String filePath) {
        try {
            // 파일을 byte[]로 변환
            File audioFile = new File(filePath);
            byte[] audioBytes = Files.readAllBytes(audioFile.toPath());  // 파일을 byte[]로 읽어옴

            // byte[] 데이터를 Google STT로 전송하여 텍스트 변환
            return googleSTTService.transcribeAudio(audioBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "파일 처리 중 오류가 발생했습니다.";
        }
    }
}
