package com.example.demo.configuration;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.CustomUserDetails;
import com.example.demo.repository.RoleRepo;
import com.example.demo.repository.UserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GoogleOauth2SuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private RedirectStrategy redirectStragety = new DefaultRedirectStrategy();

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // TODO Auto-generated method stub
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String email = token.getPrincipal().getAttributes().get("email").toString();
        User user = userRepo.findUserByEmail(email).orElse(null);
        if(user==null) {
            user = new User();
            user.setFirstName(token.getPrincipal().getAttributes().get("given_name").toString());
            user.setLastName(token.getPrincipal().getAttributes().get("family_name").toString());
            user.setEmail(email);
            String password = bCryptPasswordEncoder.encode(GenerationPassword.passwordRaw());
            user.setPassword(password);
            List<Role> roles = new ArrayList<>();
            roles.add(roleRepo.findById(2).get());
            user.setRoles(roles);
            userRepo.save(user);
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        String jwt = jwtTokenProvider.generateToken(customUserDetails);
        Cookie jwtCookie = new Cookie("jwt", jwt); // tạo mới một cookie
        jwtCookie.setHttpOnly(true); // đặt cookie này chỉ có thể được truy cập bởi server
        jwtCookie.setSecure(true); // đặt cookie này chỉ được gửi qua HTTPS
        jwtCookie.setPath("/"); // đặt đường dẫn mà cookie này sẽ được gửi kèm
        response.setHeader("Set-Cookie", "jwt=" + jwt + "; HttpOnly; SameSite=strict");
        response.addCookie(jwtCookie); // thêm cookie vào response
        redirectStragety.sendRedirect(request, response, "/");
    }
}

