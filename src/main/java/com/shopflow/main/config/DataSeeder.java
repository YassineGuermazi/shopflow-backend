package com.shopflow.main.config;

import com.shopflow.main.entity.Role;
import com.shopflow.main.entity.User;
import com.shopflow.main.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = User.builder()
                        .email("admin@gmail.com")
                        .motDePasse(passwordEncoder.encode("password"))
                        .prenom("Super")
                        .nom("Admin")
                        .role(Role.ADMIN)
                        .actif(true)
                        .dateCreation(LocalDateTime.now())
                        .build();
                userRepository.save(admin);
                System.out.println("Admin account created: admin@gmail.com / password");
            }
        };
    }
}
