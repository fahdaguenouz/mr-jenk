package com.buy01.product_service.config;

import com.buy01.product_service.models.Category;
import com.buy01.product_service.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DbSeeder {

    @Bean
    CommandLineRunner initCategories(CategoryRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<Category> categories = List.of(
                    Category.builder().name("Electronics").slug("electronics").build(),
                    Category.builder().name("Clothing").slug("clothing").build(),
                    Category.builder().name("Home & Garden").slug("home-garden").build(),
                    Category.builder().name("Books").slug("books").build(),
                    Category.builder().name("Sports").slug("sports").build()
                );
                repository.saveAll(categories);
                System.out.println("🌱 Database seeded: 5 default categories added.");
            }
        };
    }
}