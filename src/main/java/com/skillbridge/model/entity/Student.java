package com.skillbridge.model.entity;

import com.skillbridge.model.enums.AvailabilityMode;
import com.skillbridge.model.enums.PreferredTime;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_student_username", columnList = "username"),
    @Index(name = "idx_student_email", columnList = "email")
})
public class Student extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String department;
    private Integer semester;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String profilePicturePath;

    @Enumerated(EnumType.STRING)
    private AvailabilityMode availabilityMode = AvailabilityMode.OFFLINE;

    @Enumerated(EnumType.STRING)
    private PreferredTime preferredTime = PreferredTime.ANYTIME;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentSkill> skills = new ArrayList<>();

    @Transient
    private Double averageRating;

    @Transient
    private Integer completedSwaps;

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePicturePath() { return profilePicturePath; }
    public void setProfilePicturePath(String profilePicturePath) { this.profilePicturePath = profilePicturePath; }

    public AvailabilityMode getAvailabilityMode() { return availabilityMode; }
    public void setAvailabilityMode(AvailabilityMode availabilityMode) { this.availabilityMode = availabilityMode; }

    public PreferredTime getPreferredTime() { return preferredTime; }
    public void setPreferredTime(PreferredTime preferredTime) { this.preferredTime = preferredTime; }

    public List<StudentSkill> getSkills() { return skills; }
    public void setSkills(List<StudentSkill> skills) { this.skills = skills; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getCompletedSwaps() { return completedSwaps; }
    public void setCompletedSwaps(Integer completedSwaps) { this.completedSwaps = completedSwaps; }
}
