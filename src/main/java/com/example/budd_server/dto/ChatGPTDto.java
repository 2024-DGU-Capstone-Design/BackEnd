package com.example.budd_server.dto;

import lombok.*;

// ChatGPT Legacy 모델의 Request를 위한 DTO
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatGPTDto {
    // 사용할 모델
    private final String model = "gpt-3.5-turbo-instruct";

    // 사용할 프롬프트 명령어
    private String prompt;

    // 프롬프트의 다양성을 조절할 명령어
    private final float temperature = 0;

    // 최대 사용할 토큰
    private final int max_tokens = 1500;
}
