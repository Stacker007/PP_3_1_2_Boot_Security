package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kata.spring.boot_security.demo.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findByUsername(String username);

}
