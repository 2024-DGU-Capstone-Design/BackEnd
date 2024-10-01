package com.example.budd_server.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.LocalDate;

@Data
@Document(collection = "reports")
public class Report {
    @Id
    private String id;
    private int userId;
    private LocalDate month;
    private String title;
    private String mealStatus;
    private String healthStatus;
    private String emotionStatus;
    private String evaluation;
    private String conclusion;
}
