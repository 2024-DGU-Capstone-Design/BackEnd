package com.example.budd_server.repository;

import com.example.budd_server.entity.Response;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModalRepository extends MongoRepository<Response, String> {
    List<Response> findByUserId(int userId);
}
