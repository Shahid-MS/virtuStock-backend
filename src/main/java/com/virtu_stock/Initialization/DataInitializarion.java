package com.virtu_stock.Initialization;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.virtu_stock.Enum.Role;
import com.virtu_stock.User.User;
import com.virtu_stock.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializarion implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("ms@gmail.com").isEmpty()) {
            User admin = User.builder()
                    .email("ms@gmail.com")
                    .password(passwordEncoder.encode("ms@20"))
                    .roles(List.of(Role.ROLE_ADMIN))
                    .build();
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("aman@gmail.com").isEmpty()) {
            User user = User.builder()
                    .email("aman@gmail.com")
                    .password(passwordEncoder.encode("aman@"))
                    .roles(List.of(Role.ROLE_USER))
                    .build();
            userRepository.save(user);
        }

        if (userRepository.findByEmail("saif@gmail.com").isEmpty()) {
            User userAdmin = User.builder()
                    .email("saif@gmail.com")
                    .password(passwordEncoder.encode("saif@"))
                    .roles(List.of(Role.ROLE_USER, Role.ROLE_ADMIN))
                    .build();
            userRepository.save(userAdmin);
        }
    }
}
