//package com.example.OnlineExam.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter
//        extends OncePerRequestFilter {
//
//    private final com.example.OnlineExam.security.JwtService jwtService;
//    private final com.example.OnlineExam.security.CustomUserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authorizationHeader =
//                request.getHeader("Authorization");
//
//        if (authorizationHeader == null ||
//                !authorizationHeader.startsWith("Bearer ")) {
//
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token =
//                authorizationHeader.substring(7);
//
//        try {
//
//            String email =
//                    jwtService.extractUsername(token);
//
//            if (email != null &&
//                    SecurityContextHolder
//                            .getContext()
//                            .getAuthentication() == null) {
//
//                UserDetails userDetails =
//                        userDetailsService
//                                .loadUserByUsername(email);
//
//                if (jwtService.isTokenValid(
//                        token,
//                        userDetails)) {
//
//                    UsernamePasswordAuthenticationToken
//                            authentication =
//                            new UsernamePasswordAuthenticationToken(
//                                    userDetails,
//                                    null,
//                                    userDetails.getAuthorities()
//                            );
//
//                    authentication.setDetails(
//                            new WebAuthenticationDetailsSource()
//                                    .buildDetails(request)
//                    );
//
//                    SecurityContextHolder
//                            .getContext()
//                            .setAuthentication(
//                                    authentication
//                            );
//                }
//            }
//
//        } catch (Exception ignored) {
//            // Invalid JWT.
//            // Request will remain unauthenticated.
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}




package com.example.OnlineExam.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            log.debug("No Bearer token for {} {}",
                    request.getMethod(), request.getRequestURI());

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            String email = jwtService.extractUsername(token);

            if (email != null
                    && SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                    log.debug(
                            "JWT authenticated request to {} with authorities {}",
                            request.getRequestURI(),
                            userDetails.getAuthorities()
                    );

                } else {
                    log.warn("JWT validation failed for request to {}",
                            request.getRequestURI());
                }
            }

        } catch (Exception exception) {
            log.warn(
                    "JWT authentication failed for {}: {}",
                    request.getRequestURI(),
                    exception.getClass().getSimpleName()
            );
        }

        filterChain.doFilter(request, response);
    }
}
