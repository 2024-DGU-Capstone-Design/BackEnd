package com.example.budd_server.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleSttService {

    Dotenv dotenv = Dotenv.load();

    String googleSttApiKey = dotenv.get("google.apiKey");

    String GOOGLE_STT_API_URL = "https://speech.googleapis.com/v1/speech:recognize?key="+googleSttApiKey;

    public String processStt(String recordingUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Twilio의 녹음 파일을 가져와서 Base64 인코딩
            byte[] audioBytes = restTemplate.getForObject(recordingUrl, byte[].class);
            String encodedAudio = Base64.getEncoder().encodeToString(audioBytes);

            // Google STT API 요청 Body 생성
            Map<String, Object> body = new HashMap<>();
            body.put("audio", Map.of("content", encodedAudio));
            body.put("config", Map.of("languageCode", "ko-KR"));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // Google STT API 호출
            ResponseEntity<Map> response = restTemplate.exchange(GOOGLE_STT_API_URL, HttpMethod.POST, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            // Google STT 결과에서 텍스트 추출
            if (responseBody != null && responseBody.containsKey("results")) {
                Map<String, Object> firstResult = (Map<String, Object>) ((List<?>) responseBody.get("results")).get(0);
                Map<String, Object> alternatives = (Map<String, Object>) ((List<?>) firstResult.get("alternatives")).get(0);
                return (String) alternatives.get("transcript");
            }

            return null;  // 결과가 없을 경우 null 반환

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
