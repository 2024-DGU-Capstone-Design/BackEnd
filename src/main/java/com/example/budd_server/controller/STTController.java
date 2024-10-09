package com.example.budd_server.controller;

import com.example.budd_server.service.GoogleSTTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class STTController {

    @Autowired
    private GoogleSTTService googleSTTService;

    @PostMapping("/transcribe")
    public String transcribeWav(@RequestParam String filePath) {
        try {
            return googleSTTService.transcribeAudio(filePath);

        } catch (IOException e) {
            return "Error transcribing audio: " + e.getMessage();
        }
    }
}

