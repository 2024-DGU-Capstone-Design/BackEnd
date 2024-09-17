package com.example.budd_server.repository;

import com.example.budd_server.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByName(String name);
    Optional<User> findByUserId(int userId);

    @Query(value = "{}", fields = "{ 'userId' : 1 }")
    List<User> findAllUserIds();

    default Integer findMaxUserId() {
        List<User> users = findAllUserIds();
        return users.stream()
                .map(User::getUserId)
                .max(Integer::compareTo)
                .orElse(null);
    }
}
