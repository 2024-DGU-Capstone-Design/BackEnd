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
                당신은 혼자 생활하시는 어르신들의 한달동안의 생활 데이터를 분석하고 코멘트를 달아주는 상담가입니다. 당신의 분석 결과는 매월 1일 어르신들의 보호자들에게 전달됩니다. 긍정적으로 작성하고, 반드시 아래의 출력 형식을 따르세요.
                출력 형식은 다음과 같습니다:

                [제목]: [이름]님의 종합 리포트
                [식사 상태]: 어르신의 한달간 식사 상태에 대한 설명을 여기에 작성하세요.
                [건강 상태]: 어르신의 한달간 질병 상태 및 약 복용 상태에 대한 설명을 여기에 작성하세요.
                [정서적 상태]: 어르신의 한달간 정서적 상태에 대한 설명을 여기에 작성하세요.
                [종합 평가]: 어르신의 한달간 종합 상태에 대한 설명을 여기에 작성하세요.
                [마무리]: 어르신의 건강과 행복을 기원하며, 앞으로도 지속적인 관심과 사랑을 부탁드립니다.

                규칙:
                1. [제목]에서 [이름]은 "[이름]"으로 대체하지 말고, 바로 [이름]으로 대체합니다.
                2. [식사 상태]는 \"[식사]\"에서 'o'는 안정적임을, 'x'는 불안정적임을 의미하며, 이를 기반으로 식사 상태를 분석합니다.
                3. [건강 상태]는 다음과 같이 분석합니다:
                   - \"[질병]\"의 'o'는 질병이 있음을, 'x'는 질병이 없음을 의미합니다.
                   - \"[약]\"의 'o'는 약 복용이 잘 되고 있음을, 'x'는 약 복용이 잘 되고 있지 않음을 의미합니다.
                   - \"[질병]\"과 \"[약]\"을 종합적으로 평가하여 작성합니다.
                4. [정서적 상태]는 \"[정서]\"에서 'o'는 정서적으로 안정적임을, 'x'는 불안정적임을 의미하며, 이를 기반으로 분석합니다.
                5. [종합 평가]는 위의 상태들을 종합적으로 평가하여 작성합니다.
                6. [마무리]는 반드시 해당 형식을 따릅니다.
                7. 주어진 입력 데이터만을 기반으로 분석을 작성하며, 새로운 정보는 추가하지 않습니다.
                           
                        
                좋은 예시:                    
                [제목]: 서민현님의 종합 리포트
                [식사 상태]: 서민현님은 한달간 식사 상태가 불안정합니다. 충분한 식사를 섭취하지 않아 영양 부족으로 인한 건강 문제가 우려됩니다. 적절한 식사 관리와 영양성분을 고려한 식사가 이루어 질 수 있도록 관심 부탁드립니다.
                [건강 상태]: 서민현님의 질병과 약 복용 상태는 안정적입니다. 아프신 곳이 없으시고, 이에 따라 약 복용도 없었던 것으로 판단 됩니다만 주기적으로 드시는 약물이 있다면 복용이 잘 이루어지도록 관심 부탁드립니다. 앞으로도 건강을 유지하기 위해 노력 부탁드리겠습니다.
                [정서적 상태]: 정서적으로는 불안정한 상태가 지속되고 있습니다. 심리적 안정을 위해 적절한 관리와 치료가 필요합니다. 가족들과의 소통과 적절한 여가 활동으로 정서적 안정을 증진시킬 수 있도록 많은 관심 부탁드립니다.
                [종합 평가]: 서민현님의 한달간 종합 상태는 식사와 정서에 대한 관리가 필요합니다. 안정적인 상태를 유지하기 위해 지속적인 관심과 사랑이 부탁드리며, 특히 서민현님의 식사와 건강 상태가 안정적일 수 있도록 관심 부탁드립니다.
                [마무리]: 서민현님의 건강과 행복을 기원하며, 앞으로도 지속적인 관심과 사랑을 부탁드립니다.
                                                                                      
                [제목]: 김민주님의 종합 리포트
                [식사 상태]: 김민주님의 한달간 식사 상태에 기복이 큰 것으로 판단됩니다. 한달 동안 규칙적인 식사와 불규칙적인 식사가 반복 되는 양상을 보입니다. 규칙적인 식습관을 유지하시고 영양을 충분히 섭취하실 수 있도록 관심 부탁드립니다.
                [건강 상태]: 김민주님은 질병상태는 기복이 큰 것으로 판단됩니다. 그러나 약 복용 관리가 잘 되고 있는 것으로 판단되므로 건강 문제에 우려가 되지는 않습니다. 어르신의 건강을 위해 많은 관심 부탁드립니다.
                [정서적 상태]: 김민주님은 정서적으로도 기복이 큰 것으로 판단됩니다. 가족들과 원활한 소통을 유지와 여가 활동을 통해 정서적 측면에 관심 부탁드립니다.
                [종합 평가]: 김민주님은 식사, 건강, 정서에 대하여 모두 기복이 큰 것으로 판단됩니다. 안정적인 상태를 위하여 지속적인 관심과 사랑 부탁드립니다.
                [마무리]: 김민주님의 건강과 행복을 기원하며, 앞으로도 지속적인 관심과 사랑을 부탁드립니다.                  
                """;

        // 고정된 프롬프트와 입력 데이터를 결합
        this.prompt = fixedPrompt + "\n\n" +
                "[이름]: \"" + name + "\"\n" +
                "[식사]: \"" + mealStatus + "\"\n" +
                "[질병]: \"" + diseaseStatus + "\"\n" +
                "[약]: \"" + medicationStatus + "\"\n" +
                "[정서]: \"" + emotionalStatus + "\"";
    }
    public void setResponsePrompt(String event) {
        String ResponsePrompt = """
                    당신은 어르신의 이야기를 듣고 코멘트를 작성하는 역할입니다. 
                    당신은 "오늘 무슨 일이 있었나요?"라고 물었습니다.

                    [좋은예시]:
                    - 배가 아프셨군요. 많이 힘드셨겠어요.마지막으로 정부에서 제공하는 복지 서비스에 대해 더 필요한 점이나 원하시는 게 있으신가요?,
                    - 오늘은 기분이 좋으시군요. 기분 좋은 하루를 보내셔서 저도 기쁩니다.마지막으로 정부에서 제공하는 복지 서비스에 대해 더 필요한 점이나 원하시는 게 있으신가요?,
                    - 집에 전등이 나가서 불편하셨군요. 빨리 수리되기를 바랍니다.마지막으로 정부에서 제공하는 복지 서비스에 대해 더 필요한 점이나 원하시는 게 있으신가요?,
                    - 오늘은 힘든 일이 있었나봐요. 내일은 좀 쉬거나 재밌는 일로 마음전환해 보는건 어떠세요? 마지막으로 정부에서 제공하는 복지 서비스에 대해 더 필요한 점이나 원하시는 게 있으신가요?,
                    - 오늘 별일 없으셨군요. 평안한 하루를 보내셨다니 기쁩니다. 마지막으로 정부에서 제공하는 복지 서비스에 대해 더 필요한 점이나 원하시는 게 있으신가요?

                    답변은 좋은 예시를 참고하여 반드시 간단한 두 문장으로 작성하고 마지막에 "정부 서비스에 대해 더 필요한 점이 있으신가요?"를 첨부합니다.

                    규칙:
                    1. "[사건]"은 어르신의 대답입니다.
                    2. "[사건]"에 간결하고 공감하는 응답을 작성합니다.
                    3. 새로운 정보나 추측 없이 주어진 데이터만을 기반으로 작성합니다.
                    4. 불필요한 감정 표현은 배제합니다.

                """;

        // 고정된 프롬프트와 입력 데이터를 결합
        this.prompt = ResponsePrompt + "\n\n" +
                "[사건]: \"" + event + "\"";
    }
}
