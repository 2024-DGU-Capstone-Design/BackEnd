package com.example.budd_server.repository;

import com.example.budd_server.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByName(String name);
    Optional<User> findByUserId(int userId);
}
