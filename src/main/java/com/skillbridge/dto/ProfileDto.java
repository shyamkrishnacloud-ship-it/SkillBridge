package com.skillbridge.dto;

import com.skillbridge.model.enums.AvailabilityMode;
import com.skillbridge.model.enums.PreferredTime;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ProfileDto {

    private String username;
    private String email;

    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;

    private Integer semester;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    private AvailabilityMode availabilityMode;
    private PreferredTime preferredTime;

    private MultipartFile profilePicture;
    private String existingProfilePicturePath;
    
    private Double averageRating;

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public AvailabilityMode getAvailabilityMode() { return availabilityMode; }
    public void setAvailabilityMode(AvailabilityMode availabilityMode) { this.availabilityMode = availabilityMode; }

    public PreferredTime getPreferredTime() { return preferredTime; }
    public void setPreferredTime(PreferredTime preferredTime) { this.preferredTime = preferredTime; }

    public MultipartFile getProfilePicture() { return profilePicture; }
    public void setProfilePicture(MultipartFile profilePicture) { this.profilePicture = profilePicture; }

    public String getExistingProfilePicturePath() { return existingProfilePicturePath; }
    public void setExistingProfilePicturePath(String existingProfilePicturePath) { this.existingProfilePicturePath = existingProfilePicturePath; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
}
