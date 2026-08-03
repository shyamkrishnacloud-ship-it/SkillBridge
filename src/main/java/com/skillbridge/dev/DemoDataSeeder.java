package com.skillbridge.dev;

// =============================================================================
// ⚠️  DEVELOPMENT ONLY — REMOVE THIS FILE BEFORE PRODUCTION DEPLOYMENT ⚠️
// =============================================================================

import com.skillbridge.model.entity.*;
import com.skillbridge.model.enums.*;
import com.skillbridge.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * ⚠️  DEVELOPMENT ONLY ⚠️
 *
 * Populates the database with realistic demo data for presentation purposes.
 * Endpoint: POST /dev/seed-demo
 *
 * IMPORTANT: Remove this class and the /dev/** security permit BEFORE production deployment.
 */
@RestController
@RequestMapping("/dev")
public class DemoDataSeeder {

    // -------------------------------------------------------------------------
    // Repositories injected directly to avoid triggering business side-effects
    // -------------------------------------------------------------------------
    private final NotificationRepository notificationRepository;
    private final ReviewRepository reviewRepository;
    private final SwapRequestRepository swapRequestRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final SkillRepository skillRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public DemoDataSeeder(
            NotificationRepository notificationRepository,
            ReviewRepository reviewRepository,
            SwapRequestRepository swapRequestRepository,
            StudentSkillRepository studentSkillRepository,
            SkillRepository skillRepository,
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder) {
        this.notificationRepository = notificationRepository;
        this.reviewRepository = reviewRepository;
        this.swapRequestRepository = swapRequestRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.skillRepository = skillRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================================
    // POST /dev/seed-demo
    // =========================================================================
    @PostMapping("/seed-demo")
    @Transactional
    public ResponseEntity<Map<String, Object>> seedDemoData() {

        Map<String, Object> result = new LinkedHashMap<>();

        // ------------------------------------------------------------------
        // STEP 1 — Clear all existing data in FK-safe order
        // ------------------------------------------------------------------
        notificationRepository.deleteAll();
        notificationRepository.flush();

        reviewRepository.deleteAll();
        reviewRepository.flush();

        swapRequestRepository.deleteAll();
        swapRequestRepository.flush();

        studentSkillRepository.deleteAll();
        studentSkillRepository.flush();

        // Remove all students (cascade removes their skill assignments)
        studentRepository.deleteAll();
        studentRepository.flush();

        // Remove shared skill catalogue
        skillRepository.deleteAll();
        skillRepository.flush();

        // Reset auto-increment counters (MySQL-specific; safe to fail silently)
        try {
            entityManager.createNativeQuery("ALTER TABLE notifications AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE reviews AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE swap_requests AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE student_skills AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE students AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE skills AUTO_INCREMENT = 1").executeUpdate();
        } catch (Exception ignored) {
            // Auto-increment reset is best-effort; continue seeding
        }

        result.put("step1_clear", "✔ All existing data cleared");

        // ------------------------------------------------------------------
        // STEP 2 — Create demo users with BCrypt-encoded passwords
        // ------------------------------------------------------------------
        Student alex   = createStudent("alex.carter",   "Alex@123",    "alex.carter@gmail.com",   "9876543210",
                "Computer Science",      4, AvailabilityMode.BOTH,    PreferredTime.ANYTIME,
                "Java backend developer passionate about Spring Boot and APIs.");

        Student emma   = createStudent("emma.wilson",   "Emma@123",    "emma.wilson@gmail.com",   "9123456789",
                "Information Technology", 4, AvailabilityMode.ONLINE,  PreferredTime.WEEKEND,
                "UI/UX designer who enjoys creating clean and modern interfaces.");

        Student ryan   = createStudent("ryan.miller",   "Ryan@123",    "ryan.miller@gmail.com",   "9234567890",
                "Artificial Intelligence", 5, AvailabilityMode.BOTH,   PreferredTime.WEEKDAY,
                "Python enthusiast exploring AI and machine learning.");

        Student olivia = createStudent("olivia.brown",  "Olivia@123",  "olivia.brown@gmail.com",  "9345678901",
                "Computer Science",      3, AvailabilityMode.OFFLINE,  PreferredTime.ANYTIME,
                "Frontend developer focused on responsive web applications.");

        studentRepository.saveAll(List.of(alex, emma, ryan, olivia));
        studentRepository.flush();

        result.put("step2_users", "✔ Demo users created (alex.carter, emma.wilson, ryan.miller, olivia.brown)");

        // ------------------------------------------------------------------
        // STEP 3 — Create shared skill catalogue
        // ------------------------------------------------------------------
        Map<String, Skill> skills = new LinkedHashMap<>();

        // Programming
        for (String name : List.of("Java", "Spring Boot", "Python", "Machine Learning",
                                   "SQL", "Git", "HTML/CSS", "Bootstrap", "JavaScript")) {
            skills.put(name, createSkill(name, SkillCategory.PROGRAMMING));
        }
        // Design
        for (String name : List.of("UI/UX Design", "Graphic Design", "Canva")) {
            skills.put(name, createSkill(name, SkillCategory.DESIGN));
        }

        skillRepository.saveAll(skills.values());
        skillRepository.flush();

        result.put("step3_skills", "✔ Skills created (" + skills.size() + " skills in catalogue)");

        // ------------------------------------------------------------------
        // STEP 4 — Assign skills to students
        // ------------------------------------------------------------------
        List<StudentSkill> allAssignments = new ArrayList<>();

        // Alex Carter
        allAssignments.add(ss(alex, skills.get("Java"),         SkillType.OFFERED, SkillLevel.ADVANCED));
        allAssignments.add(ss(alex, skills.get("SQL"),          SkillType.OFFERED, SkillLevel.INTERMEDIATE));
        allAssignments.add(ss(alex, skills.get("Git"),          SkillType.OFFERED, SkillLevel.INTERMEDIATE));
        allAssignments.add(ss(alex, skills.get("UI/UX Design"), SkillType.REQUIRED, SkillLevel.INTERMEDIATE));
        allAssignments.add(ss(alex, skills.get("Canva"),        SkillType.REQUIRED, SkillLevel.BEGINNER));

        // Emma Wilson
        allAssignments.add(ss(emma, skills.get("UI/UX Design"),   SkillType.OFFERED, SkillLevel.ADVANCED));
        allAssignments.add(ss(emma, skills.get("Graphic Design"),  SkillType.OFFERED, SkillLevel.ADVANCED));
        allAssignments.add(ss(emma, skills.get("Canva"),           SkillType.OFFERED, SkillLevel.ADVANCED));
        allAssignments.add(ss(emma, skills.get("Java"),            SkillType.REQUIRED, SkillLevel.INTERMEDIATE));
        allAssignments.add(ss(emma, skills.get("SQL"),             SkillType.REQUIRED, SkillLevel.BEGINNER));

        // Ryan Miller
        allAssignments.add(ss(ryan, skills.get("Python"),          SkillType.OFFERED, SkillLevel.ADVANCED));
        allAssignments.add(ss(ryan, skills.get("Machine Learning"), SkillType.OFFERED, SkillLevel.INTERMEDIATE));
        allAssignments.add(ss(ryan, skills.get("Git"),             SkillType.OFFERED, SkillLevel.INTERMEDIATE));
        allAssignments.add(ss(ryan, skills.get("Spring Boot"),     SkillType.REQUIRED, SkillLevel.BEGINNER));
        allAssignments.add(ss(ryan, skills.get("Java"),            SkillType.REQUIRED, SkillLevel.INTERMEDIATE));

        // Olivia Brown
        allAssignments.add(ss(olivia, skills.get("HTML/CSS"),        SkillType.OFFERED, SkillLevel.ADVANCED));
        allAssignments.add(ss(olivia, skills.get("Bootstrap"),       SkillType.OFFERED, SkillLevel.ADVANCED));
        allAssignments.add(ss(olivia, skills.get("JavaScript"),      SkillType.OFFERED, SkillLevel.INTERMEDIATE));
        allAssignments.add(ss(olivia, skills.get("Python"),          SkillType.REQUIRED, SkillLevel.INTERMEDIATE));
        allAssignments.add(ss(olivia, skills.get("Machine Learning"), SkillType.REQUIRED, SkillLevel.BEGINNER));

        studentSkillRepository.saveAll(allAssignments);
        studentSkillRepository.flush();

        result.put("step4_skill_assignments", "✔ Skills assigned (" + allAssignments.size() + " student-skill records)");

        // ------------------------------------------------------------------
        // STEP 5 — No swap requests, notifications, or reviews (by design)
        // ------------------------------------------------------------------
        result.put("step5_interactions", "✔ No interactions created — ready for live demo workflow");

        // ------------------------------------------------------------------
        // STEP 6 — Verification
        // ------------------------------------------------------------------
        long studentCount     = studentRepository.count();
        long skillCount       = skillRepository.count();
        long ssCount          = studentSkillRepository.count();
        long swapCount        = swapRequestRepository.count();
        long reviewCount      = reviewRepository.count();
        long notifCount       = notificationRepository.count();

        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("students",       studentCount);
        counts.put("skills",         skillCount);
        counts.put("student_skills", ssCount);
        counts.put("swap_requests",  swapCount);
        counts.put("reviews",        reviewCount);
        counts.put("notifications",  notifCount);

        result.put("step6_verification", counts);

        // ------------------------------------------------------------------
        // STEP 7 — Summary
        // ------------------------------------------------------------------
        result.put("summary", Map.of(
            "✔ Demo users created",      studentCount + " users",
            "✔ Skills created",          skillCount + " skills",
            "✔ Requests created",        swapCount + " swap requests",
            "✔ Reviews created",         reviewCount + " reviews",
            "✔ Notifications created",   notifCount + " notifications",
            "status",                    "READY FOR DEMO"
        ));

        return ResponseEntity.ok(result);
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private Student createStudent(String username, String rawPassword, String email,
                                  String phone, String dept, int semester,
                                  AvailabilityMode availability, PreferredTime preferredTime,
                                  String bio) {
        Student s = new Student();
        s.setUsername(username);
        s.setPasswordHash(passwordEncoder.encode(rawPassword));
        s.setEmail(email);
        s.setPhoneNumber(phone);
        s.setDepartment(dept);
        s.setSemester(semester);
        s.setAvailabilityMode(availability);
        s.setPreferredTime(preferredTime);
        s.setBio(bio);
        return s;
    }

    private Skill createSkill(String name, SkillCategory category) {
        Skill sk = new Skill();
        sk.setName(name);
        sk.setCategory(category);
        return sk;
    }

    private StudentSkill ss(Student student, Skill skill, SkillType type, SkillLevel level) {
        StudentSkill ss = new StudentSkill();
        ss.setStudent(student);
        ss.setSkill(skill);
        ss.setSkillType(type);
        ss.setSkillLevel(level);
        return ss;
    }
}
