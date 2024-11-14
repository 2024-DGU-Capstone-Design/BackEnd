package com.example.budd_server.dto;

import java.time.LocalDate;

public interface CommentProjection {
    String getComment();
    LocalDate getDate();
}