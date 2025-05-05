package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import java.util.HashSet;
import java.util.Set;


@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRoleIfNotFound(String roleName) {
        Role role = roleRepository.findByRole(roleName);
        if (role == null) {
            role = new Role(roleName);
            roleRepository.save(role);
        }
        return role;
    }

    public Set<Role> convertStringsToRoles(String[] rolesStrings) {
        Set<Role> roles = new HashSet<>();
        if (rolesStrings != null) {
            for (String roleName : rolesStrings) {
                roles.add(createRoleIfNotFound(roleName));

            }
        } else {
            roles.add(roleRepository.findByRole("USER"));
        }
        return roles;
    }

}
