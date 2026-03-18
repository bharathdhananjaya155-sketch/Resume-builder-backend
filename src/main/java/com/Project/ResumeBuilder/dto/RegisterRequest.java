package com.Project.ResumeBuilder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @Email(message = "email should be valid")
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "Name is required")
    @Size(min = 2,max = 15,message = "name should be in between 2 and 15")
    private String name;
    @NotBlank(message = "password is required")
    @Size(min = 6,max = 15,message = "password should be in between 6 and 15")
    private String password;
    private String profileImageUrl;
}
