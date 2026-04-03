package com.finance.dashboard;

import com.finance.dashboard.entity.Role;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("Default Admin User created: username=admin, password=admin");
        }
        
        if (!userRepository.existsByUsername("analyst")) {
            User analyst = new User();
            analyst.setUsername("analyst");
            analyst.setPassword(passwordEncoder.encode("analyst"));
            analyst.setRole(Role.ROLE_ANALYST);
            analyst.setActive(true);
            userRepository.save(analyst);
            System.out.println("Default Analyst User created: username=analyst, password=analyst");
        }
        
        if (!userRepository.existsByUsername("viewer")) {
            User viewer = new User();
            viewer.setUsername("viewer");
            viewer.setPassword(passwordEncoder.encode("viewer"));
            viewer.setRole(Role.ROLE_VIEWER);
            viewer.setActive(true);
            userRepository.save(viewer);
            System.out.println("Default Viewer User created: username=viewer, password=viewer");
        }
    }
}
