
package com.varu.springboot_learning;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        String userPassword = System.getenv("APP_USER_PASSWORD");
        String adminPassword = System.getenv("APP_ADMIN_PASSWORD");

        if (userPassword == null || userPassword.isBlank()
                || adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "APP_USER_PASSWORD and APP_ADMIN_PASSWORD must be set");
        }

        UserDetails user = User.builder()
                .username("varu")
                .password(passwordEncoder.encode(userPassword))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
       
http.authorizeHttpRequests(auth -> auth
        .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**"
        ).permitAll()
        .requestMatchers("/students/**").hasAnyRole("USER", "ADMIN")
        .requestMatchers("/courses/**").hasRole("ADMIN")
        .anyRequest().authenticated())
    .formLogin(Customizer.withDefaults());

        return http.build();
    }
}
