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
public class TTSService {

    Dotenv dotenv = Dotenv.load();
    String googleTtsApiKey = dotenv.get("google.apiKey");

    private static final String GOOGLE_TTS_API_URL = "https://texttospeech.googleapis.com/v1/text:synthesize?key=";

    public void convertTextToSpeech(String text, String fileName) {
        try {
            // 요청 Body 생성
            Map<String, Object> body = new HashMap<>();
            body.put("input", Map.of("text", text));
            body.put("voice", Map.of(
                    "languageCode", "ko-KR",
                    "ssmlGender", "NEUTRAL"
            ));
            body.put("audioConfig", Map.of("audioEncoding", "MP3"));

            // HTTP 요청 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // RestTemplate을 사용하여 POST 요청 전송
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = GOOGLE_TTS_API_URL + googleTtsApiKey;
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            // 응답 처리
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("audioContent")) {
                throw new RuntimeException("Google TTS 응답에서 audioContent를 찾을 수 없습니다.");
            }

            String audioContent = (String) responseBody.get("audioContent");

            // Base64 디코딩하여 MP3 파일로 저장
            byte[] decodedAudio = Base64.getDecoder().decode(audioContent);
            String filePath = "external/" + fileName + ".mp3";

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(decodedAudio);
            }

            System.out.println("Audio content saved to: " + filePath);

        } catch (Exception e) {
            System.err.println("Google TTS 변환 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
