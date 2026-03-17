package com.buy01.user_service.repository;



import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.buy01.user_service.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailOrUsername(String email, String username); 
    boolean existsByEmail(String email);
    boolean existsByUsername(String username); 
}