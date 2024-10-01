package com.example.budd_server.controller;

import com.example.budd_server.dto.ChatGPTDto;
import com.example.budd_server.service.ChatGPTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/chatGpt")
public class ChatGPTController {

    private final ChatGPTService chatGPTService;

    public ChatGPTController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @PostMapping("/legacyPrompt")
    public ResponseEntity<Map<String, Object>> selectLegacyPrompt(@RequestBody ChatGPTDto chatGPTDto) {
        Map<String, Object> result = chatGPTService.legacyPrompt(chatGPTDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}