package com.example.budd_server.repository;

import com.example.budd_server.entity.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByUserIdAndMonthBetween(Integer userId, LocalDate startMonth, LocalDate endMonth);

    @Query("{ 'userId': ?0, 'month': { $gte: ?1, $lt: ?2 } }")
    Report findByUserIdAndMonth(int userId, LocalDate startOfMonth, LocalDate startOfNextMonth);
}
