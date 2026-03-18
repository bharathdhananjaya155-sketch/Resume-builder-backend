package com.Project.ResumeBuilder.controller;

import com.Project.ResumeBuilder.dto.AuthResponse;
import com.Project.ResumeBuilder.dto.LoginRequest;
import com.Project.ResumeBuilder.dto.RegisterRequest;
import com.Project.ResumeBuilder.service.AuthService;
import com.Project.ResumeBuilder.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.Project.ResumeBuilder.util.AppConstant.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {

    private final AuthService authService;
    private final FileUploadService fileUploadService;

    @PostMapping(REGISTER)
    public ResponseEntity<?> request(@Valid @RequestBody RegisterRequest request){
        log. info("Inside AuthController - register(): {}", request);
        AuthResponse response =authService.register(request);
        log.info("Response from service: {}",response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);


    }

    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        log. info("Inside AuthController - verifyEmail(): {}", token);
        authService.verifyEmail(token);
        return  ResponseEntity.status(HttpStatus.OK).body(Map.of("message","Email verified Successfully"));
    }

    @PostMapping(UPLOAD_IMAGE)
    public ResponseEntity<?> uploadImage(@RequestPart("image") MultipartFile file) throws IOException {
        log. info("Inside AuthController - uploadImage()");
        Map<String, String> response = fileUploadService.uploadSingleImage(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
         AuthResponse response=authService.login(loginRequest);
         return ResponseEntity.ok(response);
    }

    @GetMapping(RESEND_VERIFICATION)
    public ResponseEntity<?> resendVerification(@RequestBody Map<String,String> requestBody){
       String email =requestBody.get("email");

       if(Objects.isNull(email)){
           return ResponseEntity.badRequest().body(Map.of("message","Email is required"));
       }
       authService.resendVerification(email);

       return ResponseEntity.ok(Map.of("success",true,"message","Verification is sent"));
    }

    @GetMapping(PROFILE)
    public ResponseEntity<?> getProfile(Authentication authentication){
        Object principleObject =authentication.getPrincipal();

        AuthResponse currentProfile =authService.getProfile(principleObject);

        return ResponseEntity.ok(currentProfile);
    }


}
