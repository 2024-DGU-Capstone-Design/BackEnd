package com.example.budd_server.impl;

import com.example.budd_server.config.ChatGPTConfig;
import com.example.budd_server.dto.ChatGPTDto;
import com.example.budd_server.entity.Report;
import com.example.budd_server.entity.Response;
import com.example.budd_server.entity.User;
import com.example.budd_server.repository.ReportRepository;
import com.example.budd_server.repository.ResponseRepository;
import com.example.budd_server.repository.UserRepository;
import com.example.budd_server.service.ChatGPTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ChatGPTServiceImpl implements ChatGPTService {

    @Autowired
    ResponseRepository responseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReportRepository reportRepository;

    private final ChatGPTConfig chatGPTConfig;

    public ChatGPTServiceImpl(ChatGPTConfig chatGPTConfig) {
        this.chatGPTConfig = chatGPTConfig;
    }

    @Value("${openai.url.legacy-prompt}")
    private String legacyPromptUrl;

    @Override
    public Map<String, Object> legacyPrompt(ChatGPTDto chatGPTDto) {

        // 토큰 정보가 포함된 Header를 가져오기
        HttpHeaders headers = chatGPTConfig.httpHeaders();

        // 통신을 위한 RestTemplate을 구성
        HttpEntity<ChatGPTDto> requestEntity = new HttpEntity<>(chatGPTDto, headers);
        ResponseEntity<String> response = chatGPTConfig
                .restTemplate()
                .exchange(legacyPromptUrl, HttpMethod.POST, requestEntity, String.class);

        Map<String, Object> resultMap = new HashMap<>();
        try {
            ObjectMapper om = new ObjectMapper();
            // String -> HashMap 역직렬화를 구성
            resultMap = om.readValue(response.getBody(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.debug("JsonMappingException :: " + e.getMessage());
        } catch (RuntimeException e) {
            log.debug("RuntimeException :: " + e.getMessage());
        }
        return resultMap;
    }

    // 이번 달의 데이터를 가공하여 입력 형식으로 변환
    public ChatGPTDto generateUserReport(int userId) {
        LocalDate now = LocalDate.now();
        // 이전 달의 첫 번째 날과 마지막 날 설정
        LocalDate startDate = now.minusMonths(1).withDayOfMonth(1);
        LocalDate endDate = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

        // MongoDB에서 해당 유저의 이전 달 리포트가 있는지 확인
        Optional<Report> existingReport = reportRepository.findByUserIdAndMonth(userId, now.minusMonths(1));
        if (existingReport.isPresent()) {
            System.out.println("이미 리포트가 존재합니다: " + existingReport.get().getTitle());
            return null;
        }

        // MongoDB에서 해당 유저의 이전 달 데이터를 가져옴
        List<Response> userData = responseRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        System.out.println("User Data: " + userData); // 데이터 확인

        // 사용자 이름 가져오기
        Optional<User> userOptional = userRepository.findByUserId(userId);
        String userName = userOptional.map(User::getName).orElse("알 수 없음"); // Optional 처리

        // 각 필드를 가공하여 o/x 형식으로 변환
        String mealStatus = convertStatus(userData, "meal");
        String diseaseStatus = convertStatus(userData, "disease");
        String medicationStatus = convertStatus(userData, "medicine");
        String emotionalStatus = convertStatus(userData, "mood");

        // ChatGPTDto 객체 생성
        ChatGPTDto chatGPTDto = new ChatGPTDto();
        chatGPTDto.setPrompt(userName, mealStatus, diseaseStatus, medicationStatus, emotionalStatus);

        // 생성된 입력 데이터 출력
        System.out.println("Generated Prompt: " + chatGPTDto.getPrompt());

        // ChatGPT API 호출하여 결과 가져오기
        Map<String, Object> chatGPTResponse = legacyPrompt(chatGPTDto);

        // 리포트 객체 생성
        Report report = new Report();
        report.setUserId(userId);
        report.setMonth(now.minusMonths(1));
        report.setTitle(userName + "님의 종합 리포트");

        // API 응답에서 텍스트 내용 추출
        List<Map<String, Object>> choices = (List<Map<String, Object>>) chatGPTResponse.get("choices");
        if (choices != null && !choices.isEmpty()) {
            String responseText = (String) choices.get(0).get("text");

            report.setMealStatus(extractStatus(responseText, "식사 상태"));
            report.setHealthStatus(extractStatus(responseText, "건강 상태"));
            report.setEmotionStatus(extractStatus(responseText, "정서적 상태"));
            report.setEvaluation(extractStatus(responseText, "종합 평가"));
        } else {
            System.out.println("ChatGPT 응답이 유효하지 않습니다.");
            report.setMealStatus("정보 없음");
            report.setHealthStatus("정보 없음");
            report.setEmotionStatus("정보 없음");
            report.setEvaluation("정보 없음");
        }

        report.setConclusion(userName + "님의 건강과 행복을 기원하며, 앞으로도 지속적인 관심과 사랑을 부탁드립니다");

        // 리포트 저장
        reportRepository.save(report);

        // 저장된 리포트 내용 확인
        System.out.println("리포트 저장 완료:");
        System.out.println("제목: " + report.getTitle());
        System.out.println("식사 상태: " + report.getMealStatus());
        System.out.println("건강 상태: " + report.getHealthStatus());
        System.out.println("정서적 상태: " + report.getEmotionStatus());
        System.out.println("종합 평가: " + report.getEvaluation());
        System.out.println("마무리: " + report.getConclusion());

        return chatGPTDto;
    }

    // 각 필드의 o/x 변환 로직
    private String convertStatus(List<Response> userData, String field) {
        StringBuilder status = new StringBuilder();

        for (Response data : userData) {
            String result = switch (field) {
                case "meal" -> data.getMeal() ? "o" : "x";
                case "disease" -> data.getDisease() ? "o" : "x";
                case "medicine" -> data.getMedicine() ? "o" : "x";
                case "mood" -> data.getMood() ? "o" : "x";
                default -> ""; // Handle unexpected field cases if necessary
            };
            status.append(result);
        }

        return status.toString();
    }

    private String extractStatus(String responseText, String statusType) {
        // 상태 텍스트를 정규 표현식이나 간단한 문자열 탐색을 통해 추출
        String[] lines = responseText.split("\n");
        for (String line : lines) {
            if (line.contains("[" + statusType + "]")) {
                // 상태 정보를 반환 (예: "박영희님의 한달간 식사 상태는 안정적입니다."와 같은 전체 문장을 반환)
                return line.substring(line.indexOf(":") + 1).trim();
            }
        }
        return "정보 없음"; // 해당 상태 정보가 없을 경우
    }
}