package com.example.budd_server.service;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

@Service
public class GoogleSTTService {

    // byte[] 데이터를 처리하도록 메서드 수정
    public String transcribeAudio(byte[] audioBytes) throws Exception {
        try (SpeechClient speechClient = SpeechClient.create()) {
            // Google STT 요청 설정
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)  // Audio encoding 설정 (보통 LINEAR16)
                    .setSampleRateHertz(16000)  // 샘플 레이트 설정
                    .setLanguageCode("ko-KR")  // 한국어 설정
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(audioBytes))  // byte[] 데이터를 STT로 전송
                    .build();

            // Google STT 호출
            RecognizeResponse response = speechClient.recognize(config, audio);

            // 결과 추출 및 반환
            StringBuilder transcription = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                String transcript = result.getAlternativesList().get(0).getTranscript();
                transcription.append(transcript);
                System.out.println("Transcribed Text: " + transcript);
            }

            return transcription.toString();
        }
    }
}
