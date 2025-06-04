package ru.kata.spring.boot_security.demo.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public class UserDTO {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String name;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)

    private List<String> roles;

    public UserDTO() {}

    public UserDTO(Integer id, String username, String password, String email, String name, List<String> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}