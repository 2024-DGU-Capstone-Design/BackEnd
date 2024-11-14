package com.example.budd_server.repository;

import com.example.budd_server.dto.CommentProjection;
import com.example.budd_server.entity.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResponseRepository extends MongoRepository<Response, String> {
    boolean existsByUserIdAndDate(int userId, LocalDate date);

    List<Response> findByUserIdAndDateBetween(int userId, LocalDate startDate, LocalDate endDate);

    List<Response> findByDateBetween(LocalDateTime start, LocalDateTime end);

    Response findByUserIdAndDate(int userId, LocalDate date);

    @Query("{ 'comment': { $exists: true, $ne: \"\" } }")
    Page<CommentProjection> findByCommentIsNotNull(Pageable pageable);
}
