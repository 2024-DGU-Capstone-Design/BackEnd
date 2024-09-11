package com.example.budd_server.entity;

        import org.springframework.data.annotation.Id;
        import org.springframework.data.mongodb.core.mapping.Document;
        import lombok.*;

        import java.time.LocalDate;

@Data
@Document(collection = "responses")
public class Response {
    @Id
    private String id;
    private int userId;
    private LocalDate date;
    private boolean meal;
    private boolean disease;
    private boolean medicine;
    private boolean mood;
}