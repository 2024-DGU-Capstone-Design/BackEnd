package com.example.budd_server.service;

import com.example.budd_server.dto.ChatGPTDto;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ChatGPTService {
    Map<String, Object> legacyPrompt(ChatGPTDto chatGPTDto);

    ChatGPTDto generateUserReport(int userId);
}
