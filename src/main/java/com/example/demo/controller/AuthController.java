package com.example.demo.controller;

import com.example.demo.configuration.JwtTokenProvider;
import com.example.demo.repository.CustomUserDetails;
import com.example.demo.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String authenticateUser(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());
            System.out.println(jwt);
            Cookie jwtCookie = new Cookie("jwt", jwt); // tạo mới một cookie
            jwtCookie.setHttpOnly(true); // đặt cookie này chỉ có thể được truy cập bởi server
            jwtCookie.setSecure(true); // đặt cookie này chỉ được gửi qua HTTPS
            jwtCookie.setPath("/"); // đặt đường dẫn mà cookie này sẽ được gửi kèm
            response.setHeader("Set-Cookie", "jwt=" + jwt + "; HttpOnly; SameSite=strict");
            response.addCookie(jwtCookie); // thêm cookie vào response

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return "redirect:/";
        } catch (BadCredentialsException e) {
            // Đăng nhập không thành công, reload lại trang "/login"
            System.out.println("Wrong password");
            return "redirect:/login";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPassWord() {
        return "forget-password";
    }

    @PostMapping("/forgot-password")
    public String getNewPassWord(@RequestParam String email, RedirectAttributes redirectAttributes) {
        int result = userService.getNewPassword(email);
        if (result==0) {
            redirectAttributes.addFlashAttribute("message","wrong-email");
            return "redirect:/forgetPassword";
        } else if (result==2) {
            redirectAttributes.addFlashAttribute("message","email-not-exist");
            return "redirect:/forgetPassword";
        } else {
            redirectAttributes.addFlashAttribute("forgotpassword","success");
            return "redirect:/login";
        }
    }
}

