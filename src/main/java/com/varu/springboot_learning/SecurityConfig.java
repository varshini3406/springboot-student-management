
package com.varu.springboot_learning;

import org.springframework.core.env.Environment;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

   @Bean
public UserDetailsService userDetailsService(
        PasswordEncoder passwordEncoder,
        Environment environment) {

    String userPassword = environment.getProperty("APP_USER_PASSWORD");
    String adminPassword = environment.getProperty("APP_ADMIN_PASSWORD");

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

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/students/**", "/courses/**")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers("/students/**")
                    .hasAnyRole("USER", "ADMIN")
                .requestMatchers("/courses/**")
                    .hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    String frontendUrl = System.getenv().getOrDefault(
    "FRONTEND_URL",
    "http://localhost:5173"
);

configuration.setAllowedOrigins(List.of(frontendUrl));
    configuration.setAllowedMethods(
        List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
    );
    configuration.setAllowedHeaders(
        List.of("Authorization", "Content-Type")
    );

    UrlBasedCorsConfigurationSource source =
        new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", configuration);
    return source;
}
}
