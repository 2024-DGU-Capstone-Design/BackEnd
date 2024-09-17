package com.example.budd_server.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.LocalDate;

@Data
@Document(collection = "call_records")
public class CallRecord {
    @Id
    private String id;
    private int userId;
    private LocalDate scheduledDate;
    private CallStatus status;

    public enum CallStatus {
        completed, missed, scheduled, additional, noResponse
    }
}
