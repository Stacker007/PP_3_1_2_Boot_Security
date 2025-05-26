package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

@Service
public interface UserService {
    boolean isUserExist(User user);

    User findById(int id);

    void updateUser(User user, String[] roles);
    User updateUser(User user);

    void deleteUser(int id);

    void createUser(User user, String[] roles);
    User createUser(User user);

    List<User> findAll();
}