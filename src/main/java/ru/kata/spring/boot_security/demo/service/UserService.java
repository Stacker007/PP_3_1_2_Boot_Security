package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.DTO.UserDTO;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

@Service
public interface UserService {
    List<UserDTO> findAll();

    UserDTO findById(int id);

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Integer id, UserDTO userDTO);

    boolean isUserExist(User user);


    void deleteUser(int id);

}