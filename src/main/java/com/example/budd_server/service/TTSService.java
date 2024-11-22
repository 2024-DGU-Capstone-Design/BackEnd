package com.example.budd_server.service;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;

@Service
public class TTSService {

    public String convertTextToSpeech(String text, String fileName) {
        // 파일 경로를 src/resource 아래 지정
        String ttsFilePath = "external/" + fileName + ".mp3";

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // TTS 요청 생성
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("ko-KR")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // TTS API 호출하여 음성 데이터 생성
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // 음성 파일 저장
            ByteString audioContents = response.getAudioContent();
            try (OutputStream out = new FileOutputStream(ttsFilePath)) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file " + ttsFilePath);
            }
            return ttsFilePath;

        } catch (IOException e) {
            System.err.println("TTS 변환 중 오류 발생: " + e.getMessage());
            return null;
        }
    }
    }
