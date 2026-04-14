package com.example.ec.controller;

import com.example.ec.dto.*;
import com.example.ec.service.AuthService;
import com.example.ec.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final AuthService authService;
    private final ProductService productService;
    public AuthController(AuthService authService, ProductService productService) { this.authService = authService; this.productService = productService; }
    @ModelAttribute("loginForm") public LoginForm loginForm() { return new LoginForm(); }
    @ModelAttribute("registerForm") public RegisterForm registerForm() { return new RegisterForm(); }
    @GetMapping("/login") public String loginPage() { return "login"; }
    @GetMapping("/register") public String registerPage() { return "register"; }
    @GetMapping("/admin/login") public String adminLoginPage() { return "admin-login"; }

    @PostMapping("/login")
    public String loginCustomer(@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) return "login";
        LoginResult result = authService.loginCustomer(form.getLoginId(), form.getPassword());
        if (!result.isSuccess()) { model.addAttribute("errorMessage", "ログインIDまたはパスワードが正しくありません。"); return "login"; }
        session.setAttribute("LOGIN_ROLE", result.getRole());
        session.setAttribute("LOGIN_USER_ID", result.getId());
        session.setAttribute("LOGIN_USER_NAME", result.getDisplayName());
        return "redirect:/home";
    }

    @PostMapping("/register")
    public String registerCustomer(@Valid @ModelAttribute("registerForm") RegisterForm form, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "register";
        RegisterResult result = authService.registerCustomer(form);
        if (!result.isSuccess()) { model.addAttribute("errorMessage", result.getMessage()); return "register"; }
        redirectAttributes.addFlashAttribute("successMessage", result.getMessage());
        return "redirect:/login";
    }

    @PostMapping("/admin/login")
    public String loginAdmin(@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) return "admin-login";
        LoginResult result = authService.loginAdmin(form.getLoginId(), form.getPassword());
        if (!result.isSuccess()) { model.addAttribute("errorMessage", "管理者IDまたはパスワードが正しくありません。"); return "admin-login"; }
        session.setAttribute("LOGIN_ROLE", result.getRole());
        session.setAttribute("LOGIN_USER_ID", result.getId());
        session.setAttribute("LOGIN_USER_NAME", result.getDisplayName());
        return "redirect:/admin/home";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) return "redirect:/login";
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        model.addAttribute("userId", session.getAttribute("LOGIN_USER_ID"));
        model.addAttribute("recommendedProducts", productService.getRecommendedProducts());
        return "home";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) { session.invalidate(); return "redirect:/"; }
}
