package com.example.budd_server.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleTTSService {
    Dotenv dotenv = Dotenv.load();
    String googleTtsApiKey = dotenv.get("google.apiKey");
    String basePath = dotenv.get("base.path");

    String GOOGLE_TTS_API_URL = "https://texttospeech.googleapis.com/v1/text:synthesize?key=" + googleTtsApiKey;

    public String generateTTS(String text) {
        try {
            // Google TTS 요청과 MP3 파일 저장 로직
            Map<String, Object> body = new HashMap<>();
            body.put("input", Map.of("text", text));
            body.put("voice", Map.of("languageCode", "ko-KR"));
            body.put("audioConfig", Map.of("audioEncoding", "MP3"));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(GOOGLE_TTS_API_URL, HttpMethod.POST, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            String audioContent = (String) responseBody.get("audioContent");

            // Base64로 인코딩된 MP3 파일을 디코딩하여 저장
            byte[] decodedAudio = Base64.getDecoder().decode(audioContent);
            String filePath = "src/main/resources/static/medicine.mp3";  // 로컬 경로

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(decodedAudio);
            }

            return filePath;  // Twilio가 접근할 수 있는 파일 경로 반환

        } catch (Exception e) {
            e.printStackTrace();
            return "Error in Google TTS";
        }
    }
}
