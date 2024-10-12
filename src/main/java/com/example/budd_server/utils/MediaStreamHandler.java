package com.example.budd_server.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Component
public class MediaStreamHandler extends BinaryWebSocketHandler {

    Dotenv dotenv = Dotenv.load();
    String googleApiKey = dotenv.get("google.apiKey");


    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        byte[] audioBytes = message.getPayload().array();

        // 데이터 크기를 출력하여 제대로 데이터를 받고 있는지 확인
        System.out.println("Received audio stream: " + audioBytes.length + " bytes");

        // Google STT API로 음성 데이터 처리
        String transcript = processStt(audioBytes);

        // STT 결과를 콘솔에 출력
        System.out.println("User response transcription: " + transcript);
    }


    // Google STT API 호출을 통해 음성 데이터를 텍스트로 변환
    private String processStt(byte[] audioBytes) {
        try (SpeechClient speechClient = SpeechClient.create()) {
            // Google STT 요청 구성
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)  // WAV 파일
                    .setSampleRateHertz(16000)  // 샘플레이트
                    .setLanguageCode("ko-KR")  // 한국어 설정
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(audioBytes))
                    .build();

            // STT 요청 및 응답 처리
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            if (!results.isEmpty()) {
                SpeechRecognitionAlternative alternative = results.get(0).getAlternativesList().get(0);
                return alternative.getTranscript();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "STT 변환 실패";
    }

    //웹소켓 연결 확인
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket connection established: " + session.getId());
    }


}
