package com.Project.ResumeBuilder.service;

import com.Project.ResumeBuilder.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.Project.ResumeBuilder.util.AppConstant.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {
        private final AuthService authService;

    public Map<String, Object> getTemplate(Object principal) {
        AuthResponse response=authService.getProfile(principal);


        List<String> availableTemplates;

        boolean isPremium = PREMIUM.equalsIgnoreCase(response.getSubscription());

        if (isPremium) {
            availableTemplates = List.of("01","02","03");
        } else {
            availableTemplates = Collections.singletonList("01");
        }

        Map<String, Object> restrictions = new HashMap<>();
        restrictions.put("availableTemplates", availableTemplates);
        restrictions.put("allTemplates", List.of("01","02","03"));
        restrictions.put("subscriptionPlan", response.getSubscription());
        restrictions.put("isPremium", isPremium);

        return restrictions;
    }
}
