package com.virtu_stock.Initialization;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.virtu_stock.Enum.Role;
import com.virtu_stock.User.User;
import com.virtu_stock.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializarion implements CommandLineRunner {
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        boolean datainit = false;

        if (datainit) {
            Optional<User> userOptional = userRepository.findByEmail("ms2.o.edu@gmail.com");
            User user = userOptional.get();
            List<Role> userRoles = user.getRoles();
            userRoles.removeAll(userRoles);
            // userRoles.add(Role.ROLE_ADMIN);
            userRoles.add(Role.ROLE_USER);
            userRoles.add(Role.ROLE_ADMIN);
            user.setRoles(userRoles);
            userRepository.save(user);
        }

    }
}
