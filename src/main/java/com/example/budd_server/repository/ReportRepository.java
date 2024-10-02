package com.example.budd_server.repository;

import com.example.budd_server.entity.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByUserIdAndMonthBetween(Integer userId, LocalDate startMonth, LocalDate endMonth);

    Optional<Report> findByUserIdAndMonth(int userId, LocalDate minusMonths);
}
