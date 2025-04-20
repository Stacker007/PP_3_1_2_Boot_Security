package ru.kata.spring.boot_security.demo.service;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

@Component
public class StringToRoleConverter implements Converter<String, Role> {

    private final RoleRepository roleRepository;

    public StringToRoleConverter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role convert(String source) {
        Role role = roleRepository.findByRole(source);
        if (role == null) {
            role = new Role(source);
            roleRepository.save(role);
        }
        return role;
    }
}