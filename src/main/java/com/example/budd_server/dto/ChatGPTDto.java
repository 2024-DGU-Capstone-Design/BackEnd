package com.example.budd_server.dto;

import lombok.*;

// ChatGPT Legacy 모델의 Request를 위한 DTO
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ChatGPTDto {
    // 사용할 모델
    private final String model = "gpt-3.5-turbo-instruct";

    // 사용할 프롬프트 명령어
    private String prompt;

    // 프롬프트의 다양성을 조절할 명령어
    private final float temperature = 1;

    // 최대 사용할 토큰
    private final int max_tokens = 2000;

    // 프롬프트에 데이터를 결합하는 메서드
    public void setPrompt(String name, String mealStatus, String diseaseStatus, String medicationStatus, String emotionalStatus) {
        String fixedPrompt = """
            당신은 어르신들의 생활 데이터를 분석하고 코멘트를 달아주는 상담가 입니다. 당신의 분석 결과는 보호자들에게 전달됩니다. 따라서 긍정적으로 작성합니다.

            다음의 순서로 진행합니다.

            1. [제목]: 하단의 “”으로 구분된 [이름]을 ‘[이름]님의 종합 리포트’라고 작성합니다.
            2. [식사 상태]: “”으로 구분된 [식사]를 바탕으로 한 단락으로 어르신의 한달간 식사 상태를 작성합니다. [식사]의 'o'는 식사 상태가 안정적임을 의미하고, 'x'는 식사 상태가 불안정적임을 의미합니다.
            3. [건강 상태]: “”으로 구분된 [질병]과 “”으로 구분된 [약]을 바탕으로 한 단락으로 어르신의 한달간 건강 상태를 작성합니다. [질병]의 'o'는 건강 상태가 불안정함을 의미하고, 'x'는 건강상태가 안정적임을 의미합니다. [약]의 ‘o’는 약 복용 상태가 안정적임을 의미하고 ‘x’는 복용 상태가 불안정적임을 의미합니다.
            4. [정서적 상태]: “”으로 구분된 [정서]를 바탕으로 한 단락으로 어르신의 한달간 정서적 상태를 작성합니다. [정서]의 'o'는 정서적으로 안정적임을 의미하고, 'x'는 불안정적임을 의미합니다.
            5. [종합 평가]: [식사][질병][약][정서]를 기반으로 한 단락으로 어르신의 한달간 종합 상태를 작성합니다.
            6. [마무리]: ‘[이름]님의 건강과 행복을 기원하며, 앞으로도 지속적인 관심과 사랑을 부탁드립니다.’라고 작성합니다.
            """;

        // 고정된 프롬프트와 입력 데이터를 결합
        this.prompt = fixedPrompt + "\n\n" +
                "[이름]: \"" + name + "\"\n" +
                "[식사]: \"" + mealStatus + "\"\n" +
                "[질병]: \"" + diseaseStatus + "\"\n" +
                "[약]: \"" + medicationStatus + "\"\n" +
                "[정서]: \"" + emotionalStatus + "\"";
    }
}