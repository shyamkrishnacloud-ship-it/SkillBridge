package com.skillbridge.security;

import com.skillbridge.model.entity.Student;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final Student student;

    public CustomUserDetails(Student student) {
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // For now, everyone is a basic user.
        // If you add roles to the Student entity later, map them here.
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return student.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return student.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // You can add logic based on Student properties if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return student.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return student.isActive();
    }
}
