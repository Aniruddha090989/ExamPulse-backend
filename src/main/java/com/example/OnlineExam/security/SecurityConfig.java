
package com.example.OnlineExam.security;
import jakarta.servlet.DispatcherType;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // Enable CORS processing before authentication checks.
                .cors(cors -> {})

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Browser CORS preflight requests.
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // Public authentication endpoints.
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // Admin only - create exams.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/exams"
                        ).hasRole("ADMIN")

                        // Admin only - update exams.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/exams/**"
                        ).hasRole("ADMIN")

                        // Admin only - delete exams.
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/exams/**"
                        ).hasRole("ADMIN")

                        // Admin only - create subjects.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/subjects"
                        ).hasRole("ADMIN")

                        // Admin only - delete subjects.
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/subjects/**"
                        ).hasRole("ADMIN")

                        // Admin APIs.
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // Examiner dashboard APIs.
                        .requestMatchers(
                                "/api/examiner-dashboard/**"
                        ).hasRole("ADMIN")

                        // Student APIs.
                        .requestMatchers(
                                "/api/student/**",
                                "/api/attempts/**"
                        ).hasRole("STUDENT")



                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // All remaining endpoints require authentication.
                        .anyRequest().authenticated()
                )

                .authenticationProvider(
                        authenticationProvider()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationProvider authenticationProvider) {

        return new ProviderManager(
                authenticationProvider
        );
    }
}
