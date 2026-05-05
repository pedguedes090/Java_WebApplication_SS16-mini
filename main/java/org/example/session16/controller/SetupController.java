package org.example.session16.controller;

import org.example.session16.model.entity.User;
import org.example.session16.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/setup")
public class SetupController {

    private final UserRepository userRepository;

    public SetupController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/create-admin")
    public String showCreateAdminPage(Model model) {
        // Kiểm tra xem đã có admin chưa
        Optional<User> adminExists = userRepository.findByEmailAndRole("admin@example.com", "ADMIN");
        if (adminExists.isPresent()) {
            model.addAttribute("message", "Admin account đã tồn tại!");
            return "setup/admin-exists";
        }
        return "setup/create-admin";
    }

    @PostMapping("/create-admin")
    public String createAdmin(@RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String name,
                             Model model) {
        try {
            // Kiểm tra email đã tồn tại
            if (userRepository.findByEmail(email).isPresent()) {
                model.addAttribute("error", "Email đã được sử dụng!");
                return "setup/create-admin";
            }

            // Tạo admin user
            User admin = User.builder()
                    .name(name)
                    .email(email)
                    .password(password)
                    .phone("0123456789")
                    .address("Admin Address")
                    .role("ADMIN")
                    .build();

            userRepository.save(admin);

            model.addAttribute("success", "Admin account created successfully!");
            model.addAttribute("email", email);
            model.addAttribute("password", password);

            return "setup/admin-created";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "setup/create-admin";
        }
    }
}

