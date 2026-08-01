import os

base_dir = r"C:\Users\acer\OneDrive\Documents\SkillBridge"
src_main_java = os.path.join(base_dir, "src", "main", "java", "com", "skillbridge")
src_main_resources = os.path.join(base_dir, "src", "main", "resources")

def write_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content.strip() + "\n")

# pom.xml
pom_xml = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.3</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.skillbridge</groupId>
    <artifactId>skillbridge</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>SkillBridge</name>
    <description>Student Skill Exchange Platform</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.thymeleaf.extras</groupId>
            <artifactId>thymeleaf-extras-springsecurity6</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>"""
write_file(os.path.join(base_dir, "pom.xml"), pom_xml)

# Application.java
app_java = """package com.skillbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SkillBridgeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkillBridgeApplication.class, args);
    }
}"""
write_file(os.path.join(src_main_java, "SkillBridgeApplication.java"), app_java)

# application.properties
props = """spring.application.name=SkillBridge

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/skillbridge_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
"""
write_file(os.path.join(src_main_resources, "application.properties"), props)

# Enums
enums = {
    "AvailabilityMode": "ONLINE, OFFLINE, BOTH",
    "PreferredTime": "WEEKDAY, WEEKEND, ANYTIME",
    "NotificationType": "REQUEST, REVIEW, SYSTEM",
    "SkillCategory": "PROGRAMMING, DESIGN, MUSIC, LANGUAGES, PHOTOGRAPHY, SPORTS, BUSINESS, OTHERS",
    "SkillLevel": "BEGINNER, INTERMEDIATE, ADVANCED",
    "SkillType": "OFFERED, REQUIRED",
    "RequestStatus": "PENDING, ACCEPTED, REJECTED, COMPLETED, CANCELED"
}

for enum_name, enum_values in enums.items():
    content = f"""package com.skillbridge.model.enums;

public enum {enum_name} {{
    {enum_values}
}}"""
    write_file(os.path.join(src_main_java, "model", "enums", f"{enum_name}.java"), content)

# Entities
base_entity = """package com.skillbridge.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}"""
write_file(os.path.join(src_main_java, "model", "entity", "BaseEntity.java"), base_entity)

student = """package com.skillbridge.model.entity;

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
}"""
write_file(os.path.join(src_main_java, "model", "entity", "Student.java"), student)

skill = """package com.skillbridge.model.entity;

import com.skillbridge.model.enums.SkillCategory;
import jakarta.persistence.*;

@Entity
@Table(name = "skills", indexes = {
    @Index(name = "idx_skill_name", columnList = "name")
})
public class Skill extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillCategory category;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public SkillCategory getCategory() { return category; }
    public void setCategory(SkillCategory category) { this.category = category; }
}"""
write_file(os.path.join(src_main_java, "model", "entity", "Skill.java"), skill)

student_skill = """package com.skillbridge.model.entity;

import com.skillbridge.model.enums.SkillLevel;
import com.skillbridge.model.enums.SkillType;
import jakarta.persistence.*;

@Entity
@Table(name = "student_skills", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "skill_id", "skill_type"})
}, indexes = {
    @Index(name = "idx_studentskill_student", columnList = "student_id"),
    @Index(name = "idx_studentskill_skill", columnList = "skill_id")
})
public class StudentSkill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false)
    private SkillType skillType;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", nullable = false)
    private SkillLevel skillLevel;

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public SkillType getSkillType() { return skillType; }
    public void setSkillType(SkillType skillType) { this.skillType = skillType; }

    public SkillLevel getSkillLevel() { return skillLevel; }
    public void setSkillLevel(SkillLevel skillLevel) { this.skillLevel = skillLevel; }
}"""
write_file(os.path.join(src_main_java, "model", "entity", "StudentSkill.java"), student_skill)

swap_request = """package com.skillbridge.model.entity;

import com.skillbridge.model.enums.RequestStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "swap_requests", indexes = {
    @Index(name = "idx_swap_requester", columnList = "requester_id"),
    @Index(name = "idx_swap_receiver", columnList = "receiver_id"),
    @Index(name = "idx_swap_status", columnList = "status")
})
public class SwapRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Student requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Student receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offered_skill_id", nullable = false)
    private StudentSkill offeredSkill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_skill_id", nullable = false)
    private StudentSkill requestedSkill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    private boolean completedByRequester = false;
    private boolean completedByReceiver = false;

    public Student getRequester() { return requester; }
    public void setRequester(Student requester) { this.requester = requester; }

    public Student getReceiver() { return receiver; }
    public void setReceiver(Student receiver) { this.receiver = receiver; }

    public StudentSkill getOfferedSkill() { return offeredSkill; }
    public void setOfferedSkill(StudentSkill offeredSkill) { this.offeredSkill = offeredSkill; }

    public StudentSkill getRequestedSkill() { return requestedSkill; }
    public void setRequestedSkill(StudentSkill requestedSkill) { this.requestedSkill = requestedSkill; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public boolean isCompletedByRequester() { return completedByRequester; }
    public void setCompletedByRequester(boolean completedByRequester) { this.completedByRequester = completedByRequester; }

    public boolean isCompletedByReceiver() { return completedByReceiver; }
    public void setCompletedByReceiver(boolean completedByReceiver) { this.completedByReceiver = completedByReceiver; }
}"""
write_file(os.path.join(src_main_java, "model", "entity", "SwapRequest.java"), swap_request)

review = """package com.skillbridge.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"swap_request_id", "reviewer_id"})
}, indexes = {
    @Index(name = "idx_review_swap", columnList = "swap_request_id"),
    @Index(name = "idx_review_reviewee", columnList = "reviewee_id")
})
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swap_request_id", nullable = false)
    private SwapRequest swapRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Student reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private Student reviewee;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    public SwapRequest getSwapRequest() { return swapRequest; }
    public void setSwapRequest(SwapRequest swapRequest) { this.swapRequest = swapRequest; }

    public Student getReviewer() { return reviewer; }
    public void setReviewer(Student reviewer) { this.reviewer = reviewer; }

    public Student getReviewee() { return reviewee; }
    public void setReviewee(Student reviewee) { this.reviewee = reviewee; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}"""
write_file(os.path.join(src_main_java, "model", "entity", "Review.java"), review)

notification = """package com.skillbridge.model.entity;

import com.skillbridge.model.enums.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_student", columnList = "student_id")
})
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private boolean isRead = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { this.isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}"""
write_file(os.path.join(src_main_java, "model", "entity", "Notification.java"), notification)

# Repositories
student_repo = """package com.skillbridge.repository;

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
}"""
write_file(os.path.join(src_main_java, "repository", "StudentRepository.java"), student_repo)

skill_repo = """package com.skillbridge.repository;

import com.skillbridge.model.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByNameAndIsActiveTrue(String name);
    List<Skill> findAllByIsActiveTrue();
}"""
write_file(os.path.join(src_main_java, "repository", "SkillRepository.java"), skill_repo)

student_skill_repo = """package com.skillbridge.repository;

import com.skillbridge.model.entity.StudentSkill;
import com.skillbridge.model.enums.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {
    List<StudentSkill> findByStudentIdAndSkillTypeAndIsActiveTrue(Long studentId, SkillType skillType);
    List<StudentSkill> findByStudentIdAndIsActiveTrue(Long studentId);
}"""
write_file(os.path.join(src_main_java, "repository", "StudentSkillRepository.java"), student_skill_repo)

swap_request_repo = """package com.skillbridge.repository;

import com.skillbridge.model.entity.SwapRequest;
import com.skillbridge.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {
    
    @Query("SELECT COUNT(s) FROM SwapRequest s WHERE (s.requester.id = :studentId OR s.receiver.id = :studentId) AND s.status = 'COMPLETED' AND s.isActive = true")
    Integer countCompletedSwapsByStudentId(@Param("studentId") Long studentId);
    
    List<SwapRequest> findByReceiverIdAndStatusAndIsActiveTrue(Long receiverId, RequestStatus status);
    List<SwapRequest> findByRequesterIdAndIsActiveTrue(Long requesterId);
    List<SwapRequest> findByReceiverIdAndIsActiveTrue(Long receiverId);
}"""
write_file(os.path.join(src_main_java, "repository", "SwapRequestRepository.java"), swap_request_repo)

review_repo = """package com.skillbridge.repository;

import com.skillbridge.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :studentId AND r.isActive = true")
    Double calculateAverageRatingByStudentId(@Param("studentId") Long studentId);
    
    List<Review> findByRevieweeIdAndIsActiveTrue(Long revieweeId);
}"""
write_file(os.path.join(src_main_java, "repository", "ReviewRepository.java"), review_repo)

notification_repo = """package com.skillbridge.repository;

import com.skillbridge.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<Notification> findByStudentIdAndIsReadFalse(Long studentId);
}"""
write_file(os.path.join(src_main_java, "repository", "NotificationRepository.java"), notification_repo)

# Security Config
security_config = """package com.skillbridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}"""
write_file(os.path.join(src_main_java, "config", "SecurityConfig.java"), security_config)

print("Project generated successfully!")
