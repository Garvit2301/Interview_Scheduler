package com.scheduler.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Main Application Entry Point
 * Interview Scheduler System with Race Condition Handling
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("\n" +
                "╔════════════════════════════════════════════════════════════╗\n" +
                "║                                                            ║\n" +
                "║     Interview Scheduler Application Started Successfully  ║\n" +
                "║                                                            ║\n" +
                "║  🌐 Application: http://localhost:8080                    ║\n" +
                "║  📚 Swagger UI: http://localhost:8080/swagger-ui.html     ║\n" +
                "║  📊 API Docs: http://localhost:8080/api-docs              ║\n" +
                "║                                                            ║\n" +
                "╚════════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Configure CORS to allow frontend access
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .maxAge(3600);
            }
        };
    }
}