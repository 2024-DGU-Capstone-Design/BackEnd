package com.example.budd_server.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleTTSService {
    Dotenv dotenv = Dotenv.load();
    String googleTtsApiKey = dotenv.get("google.apiKey");
    String basePath = dotenv.get("base.path");

    String GOOGLE_TTS_API_URL = "https://texttospeech.googleapis.com/v1/text:synthesize?key=" + googleTtsApiKey;

    public String generateTTS(String text, String filename) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("input", Map.of("text", text));
            body.put("voice", Map.of("languageCode", "ko-KR"));
            body.put("audioConfig", Map.of("audioEncoding", "MP3"));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(
                    GOOGLE_TTS_API_URL, HttpMethod.POST, entity, Map.class);

            String audioContent = (String) response.getBody().get("audioContent");
            byte[] decodedAudio = Base64.getDecoder().decode(audioContent);

            String filePath = basePath + "/" + filename;
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(decodedAudio);
            }
            return filePath;  // 저장된 파일 경로 반환
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("TTS 변환 오류: " + e.getMessage());
        }
    }

    public void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
            System.out.println("파일 삭제 완료: " + filePath);
        } catch (Exception e) {
            System.err.println("파일 삭제 실패: " + e.getMessage());
        }
    }
}
