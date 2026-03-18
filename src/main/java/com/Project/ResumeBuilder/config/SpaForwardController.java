package com.Project.ResumeBuilder.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * For single-deploy production: serve the React SPA from Spring Boot static resources
 * and forward all non-API routes to index.html so client-side routing works.
 */
@Controller
public class SpaForwardController {

    @GetMapping(value = {
            "/{path:^(?!api$).*$}",
            "/{path:^(?!api$).*$}/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}

