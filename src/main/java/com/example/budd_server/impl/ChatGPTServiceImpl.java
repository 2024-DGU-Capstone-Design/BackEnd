package com.example.budd_server.impl;

import com.example.budd_server.config.ChatGPTConfig;
import com.example.budd_server.dto.ChatGPTDto;
import com.example.budd_server.entity.Response;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.ResponseRepository;
import com.example.budd_server.repository.UserRepository;
import com.example.budd_server.service.ChatGPTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ChatGPTServiceImpl implements ChatGPTService {

    @Autowired
    ResponseRepository responseRepository;

    @Autowired
    UserRepository userRepository;

    private final ChatGPTConfig chatGPTConfig;

    public ChatGPTServiceImpl(ChatGPTConfig chatGPTConfig) {
        this.chatGPTConfig = chatGPTConfig;
    }

    @Value("${openai.url.legacy-prompt}")
    private String legacyPromptUrl;

    @Override
    public Map<String, Object> legacyPrompt(ChatGPTDto chatGPTDto) {

        // 토큰 정보가 포함된 Header를 가져오기
        HttpHeaders headers = chatGPTConfig.httpHeaders();

        // 통신을 위한 RestTemplate을 구성
        HttpEntity<ChatGPTDto> requestEntity = new HttpEntity<>(chatGPTDto, headers);
        ResponseEntity<String> response = chatGPTConfig
                .restTemplate()
                .exchange(legacyPromptUrl, HttpMethod.POST, requestEntity, String.class);

        Map<String, Object> resultMap = new HashMap<>();
        try {
            ObjectMapper om = new ObjectMapper();
            // String -> HashMap 역직렬화를 구성
            resultMap = om.readValue(response.getBody(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.debug("JsonMappingException :: " + e.getMessage());
        } catch (RuntimeException e) {
            log.debug("RuntimeException :: " + e.getMessage());
        }
        return resultMap;
    }

    // 이번 달의 데이터를 가공하여 입력 형식으로 변환
    public ChatGPTDto generateUserReport(int userId) {
        LocalDate now = LocalDate.now();
        // 이전 달의 첫 번째 날과 마지막 날 설정
        LocalDate startDate = now.minusMonths(1).withDayOfMonth(1);
        LocalDate endDate = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

        // MongoDB에서 해당 유저의 이전 달 데이터를 가져옴
        List<Response> userData = responseRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        System.out.println("User Data: " + userData); // 데이터 확인

        // 사용자 이름 가져오기
        Optional<User> userOptional = userRepository.findByUserId(userId);
        String userName = userOptional.map(User::getName).orElse("알 수 없음"); // Optional 처리

        // 각 필드를 가공하여 o/x 형식으로 변환
        String mealStatus = convertStatus(userData, "meal");
        String diseaseStatus = convertStatus(userData, "disease");
        String medicationStatus = convertStatus(userData, "medicine");
        String emotionalStatus = convertStatus(userData, "mood");

        // ChatGPTDto 객체 생성
        ChatGPTDto chatGPTDto = new ChatGPTDto();
        chatGPTDto.setPrompt(userName, mealStatus, diseaseStatus, medicationStatus, emotionalStatus);

        // 생성된 입력 데이터 출력
        System.out.println("Generated Prompt: " + chatGPTDto.getPrompt());

        return chatGPTDto;
    }

    // 각 필드의 o/x 변환 로직
    private String convertStatus(List<Response> userData, String field) {
        StringBuilder status = new StringBuilder();

        for (Response data : userData) {
            String result = switch (field) {
                case "meal" -> data.getMeal() ? "o" : "x";
                case "disease" -> data.getDisease() ? "o" : "x";
                case "medicine" -> data.getMedicine() ? "o" : "x";
                case "mood" -> data.getMood() ? "o" : "x";
                default -> ""; // Handle unexpected field cases if necessary
            };
            status.append(result);
        }

        return status.toString();
    }
}
