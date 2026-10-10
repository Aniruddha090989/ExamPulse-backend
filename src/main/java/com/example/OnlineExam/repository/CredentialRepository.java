package com.example.OnlineExam.repository;

import com.example.OnlineExam.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {

    Optional<Credential> findByUsername(String username);

    Optional<Credential> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}