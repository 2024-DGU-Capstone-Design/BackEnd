package com.example.budd_server.service;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class GoogleSTTService {

    public String transcribeAudio(String mp3FilePath) throws IOException {
        try (SpeechClient speechClient = SpeechClient.create()) {
            // MP3 파일을 ByteString으로 변환
            ByteString audioBytes = ByteString.copyFrom(Files.readAllBytes(new File(mp3FilePath).toPath()));

            // Google STT 요청
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.MP3)
                    .setSampleRateHertz(16000)  // 샘플 레이트 설정 (MP3일 경우 16000Hz가 일반적)
                    .setLanguageCode("ko-KR")
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Google STT API 호출
            RecognizeResponse response = speechClient.recognize(config, audio);

            // 결과 추출 및 반환
            StringBuilder transcription = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                String transcript = result.getAlternativesList().get(0).getTranscript();
                transcription.append(transcript);

                // 변환된 텍스트를 콘솔에 출력
                System.out.println("Transcribed Text: " + transcript);
            }

            return transcription.toString();
        }
    }
}
