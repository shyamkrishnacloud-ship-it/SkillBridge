package com.skillbridge.service;

import com.skillbridge.dto.ProfileDto;
import com.skillbridge.model.entity.Student;
import com.skillbridge.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ProfileService {

    private final StudentRepository studentRepository;
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public ProfileService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public ProfileDto getProfileByUsername(String username) {
        Student student = studentRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        ProfileDto dto = new ProfileDto();
        dto.setUsername(student.getUsername());
        dto.setEmail(student.getEmail());
        dto.setDepartment(student.getDepartment());
        dto.setSemester(student.getSemester());
        dto.setBio(student.getBio());
        dto.setAvailabilityMode(student.getAvailabilityMode());
        dto.setPreferredTime(student.getPreferredTime());
        dto.setExistingProfilePicturePath(student.getProfilePicturePath());

        return dto;
    }

    @Transactional
    public void updateProfile(String username, ProfileDto profileDto) {
        Student student = studentRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setDepartment(profileDto.getDepartment());
        student.setSemester(profileDto.getSemester());
        student.setBio(profileDto.getBio());
        student.setAvailabilityMode(profileDto.getAvailabilityMode());
        student.setPreferredTime(profileDto.getPreferredTime());

        // Handle profile picture upload
        MultipartFile file = profileDto.getProfilePicture();
        if (file != null && !file.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
                String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
                Path filePath = uploadPath.resolve(fileName);
                
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                student.setProfilePicturePath("/uploads/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
            }
        }

        studentRepository.save(student);
    }
}
