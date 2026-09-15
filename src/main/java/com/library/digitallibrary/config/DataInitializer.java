package com.library.digitallibrary.config;

import com.library.digitallibrary.entity.User;
import com.library.digitallibrary.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.findByEmail("admin@library.com").isEmpty()) {

                User admin = new User();

                admin.setName("Library Admin");
                admin.setEmail("admin@library.com");
                admin.setPassword(
                    passwordEncoder.encode("admin123")
                );
                admin.setRole("ADMIN");

                userRepository.save(admin);

                System.out.println(
                    "Default Admin account created successfully."
                );
            }
        };
    }
}