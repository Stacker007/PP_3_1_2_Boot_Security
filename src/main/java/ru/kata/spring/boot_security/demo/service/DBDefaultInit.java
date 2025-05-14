package ru.kata.spring.boot_security.demo.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "demo.db.init.enabled", havingValue = "true", matchIfMissing = true)
public class DBDefaultInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleServiceImpl roleService;

    public DBDefaultInit(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleServiceImpl roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleService.createRoleIfNotFound("ADMIN");
        Role userRole = roleService.createRoleIfNotFound("USER");

        if (userRepository.findByUsername("admin") == null) {
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);

            User admin = new User();
            admin.setName("Admin");
            admin.setUsername("admin");
            admin.setEmail("admin@mail.ru");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(adminRoles);
            admin.setEnabled(true);

            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user") == null) {
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);

            User user = new User();
            user.setName("User");
            user.setUsername("user");
            user.setEmail("user@mail.ru");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRoles(userRoles);
            user.setEnabled(true);

            userRepository.save(user);
        }
    }

}
