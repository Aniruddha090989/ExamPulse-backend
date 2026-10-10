package com.example.OnlineExam.security;

import com.example.OnlineExam.model.Credential;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Credential credential;

    public CustomUserDetails(Credential credential) {
        this.credential = credential;
    }

    public Long getUserId() {
        return credential.getUserId();
    }

    public String getEmail() {
        return credential.getEmail();
    }

    @Override
    public String getUsername() {
        return credential.getEmail();
    }

    public String getDisplayUsername() {
        return credential.getUsername();
    }

    public String getRole() {
        return credential.getRole().name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + credential.getRole().name()
                )
        );
    }

    @Override
    public String getPassword() {
        return credential.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}