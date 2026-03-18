package com.Project.ResumeBuilder.service;

import com.Project.ResumeBuilder.dto.AuthResponse;
import com.Project.ResumeBuilder.dto.LoginRequest;
import com.Project.ResumeBuilder.dto.RegisterRequest;
import com.Project.ResumeBuilder.entity.User;
import com.Project.ResumeBuilder.exceptionHandler.ResourceExistsException;
import com.Project.ResumeBuilder.repositary.UserRepository;
import com.Project.ResumeBuilder.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.base.url}")
    private String appBaseUrl;

    public AuthResponse register(RegisterRequest request){
        log.info("Inside AuthService: register(): {}",request);

        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceExistsException("User already exists with this email");
        }
        else{

            User newUser = toEntity(request);

            userRepository.save(newUser);

            sendEmailVerification(newUser);

            return toResponse(newUser);


        }
    }

    private void sendEmailVerification(User newUser) {
        log.info("Inside AuthService-sendEmailVerification(): {}",newUser);
        try {
            String link=appBaseUrl+"/api/auth/verify-email?token="+newUser.getVerificationToken();
            String html="<div><h2>Verify your email</h2>" +
                    "<p>Hi " + newUser.getName() + ", please confirm your email to activate your account.</p>" +
                    "<a href='" + link + "' style='...'>Verify Email</a>" +
                    "<p>Or copy this link: " + link + "</p>" +
                    "<p>Link expires in 24 hours.</p></div>"; 

            emailService.sendHtmlEmail(newUser.getEmail(), "Verify your email", html);
        }
        catch (Exception e){
            log.info("Exception occured at sendEmailVerification():{}",e.getMessage());
            throw new RuntimeException("Failed to send verification email: "+e.getMessage());
        }
    }

    private AuthResponse toResponse(User newUser){
        return AuthResponse.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .profileImageUrl(newUser.getProfileImageUrl())
                .subscription(newUser.getSubscription())
                .emailVerified(newUser.isEmailVerified())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();
    }

    private User toEntity(RegisterRequest request){
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImageUrl(request.getProfileImageUrl())
                .subscription("Basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }


    public void verifyEmail(String token) {
        log.info("Inside AuthService- verifyEmail():{}",token);
        User user=userRepository.findByVerificationToken(token)
                .orElseThrow(()-> new RuntimeException("Invalid or expired Verification "));

        if(user.getVerificationExpires() != null && user.getVerificationExpires().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Verification token has ex[ired,Please Request ");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationExpires(null);
        userRepository.save(user);

    }

    public AuthResponse login(@Valid LoginRequest loginRequest) {
        User existingUser =userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("Invalid email or password"));

        if(!passwordEncoder.matches(loginRequest.getPassword(),existingUser.getPassword())){
            throw new UsernameNotFoundException("Invalid email or password");
        }

        if(!existingUser.isEmailVerified()){
            throw new RuntimeException("Please verify your email before login");
        }
        String token = jwtUtil.generateToken(existingUser.getId());

        AuthResponse response = toResponse(existingUser);
        response.setToken(token);
        return response;
    }

    public void resendVerification(String email) {
        User user =userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));

        if(user.isEmailVerified()){
            throw new RuntimeException("Email is already verified");
        }

        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationExpires(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        sendEmailVerification(user);
    }

    public AuthResponse getProfile(Object principleObject) {
        User existingUser =(User) principleObject;
        return toResponse(existingUser);
    }
}
