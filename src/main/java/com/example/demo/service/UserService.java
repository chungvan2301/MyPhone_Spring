package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.CustomUserDetails;
import com.example.demo.repository.RoleRepo;
import com.example.demo.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JavaMailSender javaMailSender;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()_+-=";
    private static final int PASSWORD_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    public CustomUserDetails loadUserById (Long userId)  {
        User user = userRepo.findById(userId).orElse(null);
        if (user!=null) {
            return new CustomUserDetails(user);
        } else {
            return null; // or throw an exception, depending on your use case
        }
    }

    public Boolean addNewUser (User user) {
        if (userRepo.findUserByEmail(user.getEmail()).isPresent()) {
            return false;
        } else {
            String password = user.getPassword();
            user.setPassword(bCryptPasswordEncoder.encode(password));
            List<Role> roles = new ArrayList<>();
            roles.add(roleRepo.findById(2).get());
            user.setRoles(roles);
            userRepo.save(user);
            return true;
        }
    }
    public User getUser () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            return customUserDetails.getUser();
        } else return null;
    }
    public int getNewPassword (String email) {
        if (userRepo.findUserByEmail(email).orElse(null)==null) {
            System.out.println(email);
            return 0;
        } else {
            System.out.println("tesst2");
            User user = userRepo.findUserByEmail(email).orElse(null);
            String newPassword = this.generatePassword();
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepo.save(user);
            return sendNewPasswordEmail(email,newPassword);
        }

    }

    public int sendNewPasswordEmail(String to, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your new password");
        message.setText("Your new password is: " + newPassword);
        try {
            System.out.println("tesst");
            javaMailSender.send(message);
            return 1;
        } catch (MailException e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            // Return a custom message
            return 2;
        }
    }

    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }

    //Contact
    public int sendEmailToShop(String email, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("nguyenchungvan.t1.15cla@gmail.com");
        simpleMailMessage.setSubject("TIN NHẮN TỪ " + email);
        simpleMailMessage.setText("TIN NHẮN: " + message);
        try {
            System.out.println("tesst");
            javaMailSender.send(simpleMailMessage);
            return 1;
        } catch (MailException e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            // Return a custom message
            return 2;
        }
    }
}
