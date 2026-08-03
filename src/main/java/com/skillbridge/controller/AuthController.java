package com.skillbridge.controller;

import com.skillbridge.dto.RegisterDto;
import com.skillbridge.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String showLandingPage() {
        return "landing";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") RegisterDto registerDto, 
                               BindingResult bindingResult, 
                               RedirectAttributes redirectAttributes) {
                               
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registerDto", "Passwords do not match");
        }
        
        if (authService.isUsernameTaken(registerDto.getUsername())) {
            bindingResult.rejectValue("username", "error.registerDto", "Username is already taken");
        }
        
        if (authService.isEmailTaken(registerDto.getEmail())) {
            bindingResult.rejectValue("email", "error.registerDto", "Email is already registered");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        authService.registerUser(registerDto);
        
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful. Please log in.");
        return "redirect:/login";
    }
}
