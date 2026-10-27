package com.bustracking.controller;

import com.bustracking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 @RequestParam("fullName") String fullName,
                                 @RequestParam("email") String email,
                                 @RequestParam(value = "phone", required = false) String phone,
                                 Model model) {
        try {
            userService.registerUser(username, password, fullName, email, phone, "ROLE_PASSENGER");
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            return "auth/register";
        }
    }
}
