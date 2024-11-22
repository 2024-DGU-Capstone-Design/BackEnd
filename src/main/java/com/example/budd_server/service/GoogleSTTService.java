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
import java.util.Map;

@Service
public class GoogleSTTService {

    Dotenv dotenv = Dotenv.load();
    String googleSttApiKey = dotenv.get("google.apiKey");

    private static final String GOOGLE_STT_API_URL = "https://texttospeech.googleapis.com/v1/text:synthesize?key=";

    public GoogleSTTService() {
        Dotenv dotenv = Dotenv.load();
        this.googleSttApiKey = dotenv.get("google.apiKey");
    }

    public String transcribeAudio(byte[] audioBytes) {
        try {
            // Base64로 오디오 데이터를 인코딩
            String encodedAudio = Base64.getEncoder().encodeToString(audioBytes);

            // 요청 Body 생성
            Map<String, Object> config = new HashMap<>();
            config.put("encoding", "MP3");  // 오디오 인코딩
            config.put("sampleRateHertz", 16000);  // 샘플 레이트
            config.put("languageCode", "ko-KR");  // 언어 설정

            Map<String, Object> audio = new HashMap<>();
            audio.put("content", encodedAudio);  // Base64로 인코딩된 오디오 데이터

            Map<String, Object> body = new HashMap<>();
            body.put("config", config);
            body.put("audio", audio);

            // HTTP 요청 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // RestTemplate으로 POST 요청 전송
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = GOOGLE_STT_API_URL + googleSttApiKey;
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            // 응답 처리
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("results")) {
                throw new RuntimeException("Google STT 응답에서 results를 찾을 수 없습니다.");
            }

            // 결과 추출
            StringBuilder transcription = new StringBuilder();
            for (Map<String, Object> result : (Iterable<Map<String, Object>>) responseBody.get("results")) {
                Map<String, Object> alternative = ((Iterable<Map<String, Object>>) result.get("alternatives")).iterator().next();
                String transcript = (String) alternative.get("transcript");
                transcription.append(transcript).append(" ");
                System.out.println("Transcribed Text: " + transcript);
            }

            return transcription.toString().trim();

        } catch (Exception e) {
            System.err.println("Google STT 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return "Error in Google STT";
        }
    }
}
