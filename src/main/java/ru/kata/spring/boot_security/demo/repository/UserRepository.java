package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kata.spring.boot_security.demo.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findByUsername(String username);

    User findByEmail(@NotBlank(message = "Email cannot be empty") @Email(message = "Email should be valid") String email);
}
