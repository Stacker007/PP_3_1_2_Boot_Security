package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserController(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/admin")
    public String users(Model model) {

        model.addAttribute("people", userRepository.findAll());
        return "admin/index";
    }


    @GetMapping("/admin/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        return "admin/new";
    }

    @GetMapping("/user")
    public String showUserPage(@AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/user";
    }


    @PostMapping("/admin/new")
    public String createUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, @RequestParam(value = "roles", required = false) String[] roles, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());

            System.out.println(bindingResult.getAllErrors());
            return "admin/new";

        }


        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            model.addAttribute("message", "User already exists");
            return "admin/new";
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);


        Set<Role> userRoles = new HashSet<>();
        if (roles != null) {
            for (String roleName : roles) {
                Role role = roleRepository.findByRole(roleName);
                if (role == null) {
                    roleRepository.save(new Role(roleName));
                    role = roleRepository.findByRole(roleName);
                }
                if (role != null) {
                    userRoles.add(role);
                }
            }
        } else {
            userRoles.add(roleRepository.findByRole("USER"));
        }
        user.setRoles(userRoles);

        userRepository.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit")
    public String showEditForm(@RequestParam("id") Integer id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        Iterable<Role> allRoles = roleRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "/admin/edit";
    }

    @PostMapping("admin/edit")
    public String updateUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam(value = "roles", required = false) String[] roles,
            Model model) {


        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());


            System.out.println(bindingResult.getAllErrors());
            return "admin/edit";
        }

        User existingUser = userRepository.findById(user.getId()).orElseThrow();

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setUsername(user.getUsername());
        existingUser.setEnabled(user.isEnabled());

        if (!user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        Set<Role> userRoles = new HashSet<>();
        if (roles != null) {
            for (String roleName : roles) {
                Role role = roleRepository.findByRole(roleName);
                if (role == null) {
                    role = new Role(roleName);
                    roleRepository.save(role);
                }
                userRoles.add(role);
            }
        }
        existingUser.setRoles(userRoles);

        userRepository.save(existingUser);
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete")
    public String delete(@RequestParam int id) {
        userRepository.deleteById(id);
        return "redirect:/admin";
    }
}
