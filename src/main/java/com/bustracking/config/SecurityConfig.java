package com.bustracking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/ws-bus-tracking/**", "/api/**", "/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable()) // Allow frames for H2 console
            )
            .authorizeHttpRequests(auth -> auth
                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                // Authentication & registration
                .requestMatchers("/login", "/register", "/error", "/403", "/404", "/500").permitAll()
                // Public passenger tracking & catalog
                .requestMatchers("/", "/track", "/track/**", "/routes", "/routes/**", "/search", "/search/**").permitAll()
                // Public APIs for real-time tracking data & WebSocket
                .requestMatchers("/api/locations/**", "/api/eta/**", "/ws-bus-tracking/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                // Role-based authorization
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**", "/api/simulation/**").hasRole("ADMIN")
                .requestMatchers("/driver/**").hasAnyRole("DRIVER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customAuthenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isDriver = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DRIVER"));

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else if (isDriver) {
                response.sendRedirect("/driver/dashboard");
            } else {
                response.sendRedirect("/track");
            }
        };
    }
}
