package com.example.budd_server.repository;

import com.example.budd_server.entity.Response;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ResponseRepository extends MongoRepository<Response, String> {
    List<Response> findByUserIdAndDateBetween(int userId, LocalDate startDate, LocalDate endDate);
}
