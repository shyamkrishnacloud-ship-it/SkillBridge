package com.skillbridge.service;

import com.skillbridge.dto.RegisterDto;
import com.skillbridge.model.entity.Student;
import com.skillbridge.repository.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isUsernameTaken(String username) {
        return studentRepository.existsByUsername(username);
    }

    public boolean isEmailTaken(String email) {
        return studentRepository.existsByEmail(email);
    }

    @Transactional
    public void registerUser(RegisterDto registerDto) {
        Student student = new Student();
        student.setUsername(registerDto.getUsername());
        student.setEmail(registerDto.getEmail());
        student.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));
        
        studentRepository.save(student);
    }
}
