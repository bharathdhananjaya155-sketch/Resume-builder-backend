package com.Project.ResumeBuilder.controller;

import com.Project.ResumeBuilder.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;

    @PostMapping(value = "/send-resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> sendResumeByEmail(
            @RequestPart("recipientEmail") String recipientEmail,
            @RequestPart(value = "subject", required = false) String subject,
            @RequestPart(value = "message", required = false) String message,
            @RequestPart("pdfFile") MultipartFile pdfFile,
            Authentication authentication) throws IOException, MessagingException {

        if (Objects.isNull(recipientEmail) || Objects.isNull(pdfFile)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "input fields missing the required fields");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        byte[] pdfBytes = pdfFile.getBytes();
        String originalFileName = pdfFile.getOriginalFilename();
        String fileName = Objects.nonNull(originalFileName) ? originalFileName : "resume.pdf";

        String emailSubject = Objects.nonNull(subject) ? subject : "resume application";
        String emailBody = Objects.nonNull(message) ? message : "Please find my resume attached.\n\n Best regards";

        emailService.sendEmailWithAttachment(recipientEmail, emailSubject, emailBody, pdfBytes, fileName);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "resume sent successfully to " + recipientEmail);
        return ResponseEntity.ok(response);
    }
}
