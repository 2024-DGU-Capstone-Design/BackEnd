package com.example.budd_server.repository;

import com.example.budd_server.entity.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByUserIdAndMonthBetween(Integer userId, LocalDate startMonth, LocalDate endMonth);
}
