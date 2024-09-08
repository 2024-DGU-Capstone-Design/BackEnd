package com.example.budd_server.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private int age;
    private String gender;
    private String phone_number;
    private String address;
    private String contact_1;
    private String contact_2;
    private String risk_level;

}
