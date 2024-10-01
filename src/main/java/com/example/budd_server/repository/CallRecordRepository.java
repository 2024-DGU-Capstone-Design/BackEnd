package com.example.budd_server.repository;

import com.example.budd_server.entity.CallRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CallRecordRepository extends MongoRepository<CallRecord, String> {
    List<CallRecord> findByUserIdAndScheduledDateBetween(int userId, LocalDate startYear, LocalDate endYear);

    List<CallRecord> findByUserId(int userId);
}
