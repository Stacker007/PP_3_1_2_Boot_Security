package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.exception.UserNotFoundException;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserService userService;


    public UserController(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder, RoleService roleService, UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.userService = userService;
    }
    @GetMapping("/")
    public String adminPanel(Model model, Principal principal) {
        // Получаем текущего пользователя
        User currentUser = userRepository.findByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);

        // Получаем всех пользователей для админской таблицы
        model.addAttribute("people", userRepository.findAll());

        // Пустой объект для формы создания
        model.addAttribute("user", new User());

        return "index";
    }

    @GetMapping("/admin")
    public String users(Model model, Principal principal) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("auth", auth);
        model.addAttribute("people", userRepository.findAll());
        model.addAttribute("activeTab", "users");
        model.addAttribute("user", new User());
        return "admin/index";
    }


    @GetMapping("/admin/new")
    public String newUser(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("auth", auth);
        model.addAttribute("user", new User());
        model.addAttribute("activeTab", "new");
        return "admin/index";
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

        if (userService.isUserExist(user)) {
            model.addAttribute("message", "User already exists");
            return "admin/new";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roleService.convertStringsToRoles(roles));

        userRepository.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit")
    public String showEditForm(@RequestParam("id") Integer id, Model model) {
        User user = userService.findById(id);
        Iterable<Role> allRoles = roleRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "/admin/edit";
    }

    @PostMapping("admin/edit")
    public String updateUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, @RequestParam(value = "roles", required = false) String[] roles, Model model) {


        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());


            System.out.println(bindingResult.getAllErrors());
            return "admin/edit";
        }

        try {
            User existingUser = userService.findById(user.getId());

            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setUsername(user.getUsername());
            existingUser.setEnabled(user.isEnabled());

            if (!user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            existingUser.setRoles(roleService.convertStringsToRoles(roles));

            userService.updateUser(existingUser);
            return "redirect:/admin";
        } catch (UserNotFoundException e) {
            model.addAttribute("error", e.getMessage());
//            return "admin/edit";
            return "redirect:/admin";
        }
    }

    @PostMapping("/admin/delete")
    public String delete(@RequestParam int id, Model model) {
        try {
            userService.deleteUser(id);
            return "redirect:/admin";
        } catch (UserNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }
}
