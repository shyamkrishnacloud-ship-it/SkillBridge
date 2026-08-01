package com.skillbridge.repository;

import com.skillbridge.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsernameAndIsActiveTrue(String username);
    Optional<Student> findByEmailAndIsActiveTrue(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
