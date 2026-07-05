package com.ordersystem.order_management.config;

import com.ordersystem.order_management.model.entity.User;
import com.ordersystem.order_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@system.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));
            userRepository.save(admin);
            System.out.println("✅ Usuario admin creado: admin / admin123");
        }

        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@test.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles(Set.of("ROLE_USER"));
            userRepository.save(user);
            System.out.println("✅ Usuario user creado: user / user123");
        }
    }
}