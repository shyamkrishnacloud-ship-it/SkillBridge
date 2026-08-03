package com.skillbridge.service.impl;

import com.skillbridge.dto.MatchDto;
import com.skillbridge.model.entity.Student;
import com.skillbridge.model.enums.AvailabilityMode;
import com.skillbridge.model.enums.SkillCategory;
import com.skillbridge.model.enums.SkillType;
import com.skillbridge.repository.StudentRepository;
import com.skillbridge.service.MatchingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MatchingServiceImpl implements MatchingService {

    private final StudentRepository studentRepository;
    private final com.skillbridge.repository.ReviewRepository reviewRepository;

    public MatchingServiceImpl(StudentRepository studentRepository, com.skillbridge.repository.ReviewRepository reviewRepository) {
        this.studentRepository = studentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDto> getMatches(String currentUsername, String skillName, SkillCategory category, String department, AvailabilityMode availability) {
        Student currentUser = studentRepository.findByUsernameAndIsActiveTrue(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<String> myWants = currentUser.getSkills().stream()
                .filter(ss -> ss.getSkillType() == SkillType.REQUIRED)
                .map(ss -> ss.getSkill().getName().toLowerCase())
                .collect(Collectors.toSet());

        Set<String> myOffers = currentUser.getSkills().stream()
                .filter(ss -> ss.getSkillType() == SkillType.OFFERED)
                .map(ss -> ss.getSkill().getName().toLowerCase())
                .collect(Collectors.toSet());

        List<Student> otherStudents = studentRepository.findOtherActiveStudentsWithSkills(currentUsername);

        List<MatchDto> matches = new ArrayList<>();

        for (Student student : otherStudents) {
            // Apply filters
            if (department != null && !department.trim().isEmpty() && !department.equalsIgnoreCase(student.getDepartment())) {
                continue;
            }

            if (availability != null) {
                if (availability == AvailabilityMode.BOTH) {
                    if (student.getAvailabilityMode() != AvailabilityMode.BOTH) continue;
                } else {
                    if (student.getAvailabilityMode() != availability && student.getAvailabilityMode() != AvailabilityMode.BOTH) {
                        continue;
                    }
                }
            }

            if (category != null) {
                boolean hasCategory = student.getSkills().stream()
                        .anyMatch(ss -> ss.getSkill().getCategory() == category);
                if (!hasCategory) continue;
            }

            if (skillName != null && !skillName.trim().isEmpty()) {
                boolean hasSkill = student.getSkills().stream()
                        .anyMatch(ss -> ss.getSkill().getName().toLowerCase().contains(skillName.toLowerCase()));
                if (!hasSkill) continue;
            }

            int score = 0;
            
            List<String> theirOffers = student.getSkills().stream()
                    .filter(ss -> ss.getSkillType() == SkillType.OFFERED)
                    .map(ss -> ss.getSkill().getName())
                    .toList();
            
            long offerMatchCount = theirOffers.stream()
                    .filter(offer -> myWants.contains(offer.toLowerCase()))
                    .count();
            
            score += offerMatchCount * 50;

            List<String> theirWants = student.getSkills().stream()
                    .filter(ss -> ss.getSkillType() == SkillType.REQUIRED)
                    .map(ss -> ss.getSkill().getName())
                    .toList();
                    
            long wantMatchCount = theirWants.stream()
                    .filter(want -> myOffers.contains(want.toLowerCase()))
                    .count();
                    
            score += wantMatchCount * 50;
            
            if (offerMatchCount > 0 && wantMatchCount > 0) {
                score += 20; 
            }
            
            boolean hasFilters = (department != null && !department.trim().isEmpty()) ||
                                 (availability != null) ||
                                 (category != null) ||
                                 (skillName != null && !skillName.trim().isEmpty());
                                 
            if (!hasFilters && score == 0) {
                continue;
            }
            
            MatchDto dto = new MatchDto();
            dto.setUsername(student.getUsername());
            dto.setDepartment(student.getDepartment());
            dto.setSemester(student.getSemester());
            Double avg = reviewRepository.calculateAverageRatingByStudentId(student.getId());
            dto.setAverageRating(avg != null ? avg : 0.0);
            
            Integer rCount = reviewRepository.countReviewsByStudentId(student.getId());
            dto.setReviewCount(rCount != null ? rCount : 0);
            
            dto.setOfferedSkills(theirOffers);
            dto.setRequiredSkills(theirWants);
            dto.setMatchScore(Math.min(100, score));
            matches.add(dto);
        }

        matches.sort(Comparator.comparingInt(MatchDto::getMatchScore).reversed());

        return matches;
    }
}
