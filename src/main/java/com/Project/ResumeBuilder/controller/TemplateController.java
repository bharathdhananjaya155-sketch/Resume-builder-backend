package com.Project.ResumeBuilder.controller;

import com.Project.ResumeBuilder.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.Project.ResumeBuilder.util.AppConstant.TEMPLATE_CONTROLLER;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(TEMPLATE_CONTROLLER)
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping
    public ResponseEntity<?> getTemplate(Authentication authentication){
        Map<String,Object> response =templateService.getTemplate(authentication.getPrincipal());

        return ResponseEntity.ok(response);
    }
}
