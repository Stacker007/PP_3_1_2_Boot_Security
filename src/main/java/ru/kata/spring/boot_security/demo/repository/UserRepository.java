package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.kata.spring.boot_security.demo.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    @EntityGraph(attributePaths = {"roles"})
    User findByUsername(String username);


    @EntityGraph(attributePaths = {"roles"})
    User findByEmail(@NotBlank(message = "Email cannot be empty") @Email(message = "Email should be valid") String email);

    @Override
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findById(Integer integer);

    @Override
    @EntityGraph(attributePaths = {"roles"})
    Iterable<User> findAll();
}
