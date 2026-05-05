package org.example.session16.controller;

import jakarta.servlet.http.HttpSession;
import org.example.session16.model.dto.LoginDTO;
import org.example.session16.model.dto.RegisterDTO;
import org.example.session16.model.entity.User;
import org.example.session16.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDTO loginDTO, 
                       HttpSession session, 
                       Model model) {
        try {
            Optional<User> user = userService.login(loginDTO);
            if (user.isPresent()) {
                session.setAttribute("userId", user.get().getId());
                session.setAttribute("userName", user.get().getName());
                session.setAttribute("userEmail", user.get().getEmail());
                session.setAttribute("userRole", user.get().getRole());
                
                // Chuyển hướng admin đến dashboard admin
                if ("ADMIN".equals(user.get().getRole())) {
                    return "redirect:/admin/dashboard";
                }
                return "redirect:/";
            } else {
                model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
                model.addAttribute("loginDTO", loginDTO);
                return "auth/login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi đăng nhập: " + e.getMessage());
            model.addAttribute("loginDTO", loginDTO);
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterDTO registerDTO,
                          Model model) {
        try {
            User user = userService.register(registerDTO);
            model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            model.addAttribute("loginDTO", new LoginDTO());
            return "redirect:/auth/login?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerDTO", registerDTO);
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}

