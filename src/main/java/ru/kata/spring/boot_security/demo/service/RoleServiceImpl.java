package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role createRoleIfNotFound(String roleName) {
        Role role = roleRepository.findByRole(roleName);
        if (role == null) {
            role = new Role(roleName);
            roleRepository.save(role);
        }
        return role;
    }

    @Override
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

    @Override
    public List<Role> findAll() {
        return (List<Role>) roleRepository.findAll();
    }

    @Override
    public Role findRole(String roleName) {
        return roleRepository.findByRole(roleName);
    }

    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);

    }


}