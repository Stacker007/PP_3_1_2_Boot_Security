package ru.kata.spring.boot_security.demo.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.DTO.UserDTO;
import ru.kata.spring.boot_security.demo.exception.UserNotFoundException;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, RoleService roleService, RoleRepository roleRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }


    private void configureModelMapper() {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        modelMapper.typeMap(User.class, UserDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getRoles().stream()
                                    .map(Role::getRole)
                                    .collect(Collectors.toList()),
                            UserDTO::setRoles);
                });

    }

    private User convertToEntity(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        Set<Role> roles = userDTO.getRoles().stream()
                .map(roleService::createRoleIfNotFound)
                .collect(Collectors.toSet());
        user.setRoles(roles);
        return user;
    }
    private UserDTO convertToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public UserDTO findById(int id) {
        return userRepository.findById(id).
                map(this::convertToDto)
                .orElseThrow(()->new UserNotFoundException("User not found"));
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        user = userRepository.save(user);
        return convertToDto(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        modelMapper.map(userDTO, existingUser);
        Set<Role> roles = userDTO.getRoles().stream()
                .map(roleService::createRoleIfNotFound)
                .collect(Collectors.toSet());
        existingUser.setRoles(roles);

        return convertToDto(userRepository.save(existingUser));
    }
    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean isUserExist(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        return userFromDB != null;
    }


}