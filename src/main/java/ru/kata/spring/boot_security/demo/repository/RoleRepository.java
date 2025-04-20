package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kata.spring.boot_security.demo.model.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByRole(String role);
}
