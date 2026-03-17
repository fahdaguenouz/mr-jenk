package com.buy01.product_service.repository;

import com.buy01.product_service.models.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Optional<Category> findByName(String name);
}