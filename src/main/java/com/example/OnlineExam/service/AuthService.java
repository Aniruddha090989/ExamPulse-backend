

package com.example.OnlineExam.service;


import com.example.OnlineExam.security.CustomUserDetails;
import com.example.OnlineExam.security.JwtService;



import com.example.OnlineExam.dto.LoginRequest;
import com.example.OnlineExam.dto.LoginResponse;
import com.example.OnlineExam.dto.RegisterRequest;
import com.example.OnlineExam.dto.UserResponse;
import com.example.OnlineExam.model.Credential;
import com.example.OnlineExam.model.Role;
import com.example.OnlineExam.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public UserResponse register(RegisterRequest request) {

        if (credentialRepository
                .existsByUsername(request.username())) {

            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        if (credentialRepository
                .existsByEmail(request.email())) {

            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }

        Credential user = new Credential();

        user.setUsername(request.username());
        user.setEmail(request.email());

        user.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );

        // Public registration can only create STUDENT
        user.setRole(Role.STUDENT);

        user = credentialRepository.save(user);

        return toResponse(user);
    }


    public UserResponse toResponse(Credential user) {

        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }


    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String token =
                jwtService.generateToken(userDetails);

        Credential credential =
                credentialRepository
                        .findByEmail(request.email())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        return new LoginResponse(
                token,
                credential.getUserId(),
                credential.getUsername(),
                credential.getEmail(),
                credential.getRole().name()
        );
    }
}