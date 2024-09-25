package com.example.budd_server.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private int userId;
    private String name;
    private int age;
    private Gender gender;
    public enum Gender {
        남, 여
    }
    private String phoneNumber;
    private String address;
    private String contact1;
    private String contact2;
    private String riskLevel;
}
