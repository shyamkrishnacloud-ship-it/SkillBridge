package com.skillbridge.service.impl;

import com.skillbridge.dto.SkillDto;
import com.skillbridge.model.entity.Skill;
import com.skillbridge.model.entity.Student;
import com.skillbridge.model.entity.StudentSkill;
import com.skillbridge.repository.SkillRepository;
import com.skillbridge.repository.StudentRepository;
import com.skillbridge.repository.StudentSkillRepository;
import com.skillbridge.service.SkillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillServiceImpl implements SkillService {

    private final StudentSkillRepository studentSkillRepository;
    private final SkillRepository skillRepository;
    private final StudentRepository studentRepository;

    public SkillServiceImpl(StudentSkillRepository studentSkillRepository,
                            SkillRepository skillRepository,
                            StudentRepository studentRepository) {
        this.studentSkillRepository = studentSkillRepository;
        this.skillRepository = skillRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public List<SkillDto> getStudentSkills(String username) {
        Student student = studentRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return studentSkillRepository.findByStudentIdAndIsActiveTrue(student.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SkillDto getStudentSkill(Long id, String username) {
        Student student = studentRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentSkill studentSkill = studentSkillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student skill not found"));

        if (!studentSkill.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Not authorized to view this skill");
        }

        return mapToDto(studentSkill);
    }

    @Override
    @Transactional
    public void addStudentSkill(String username, SkillDto skillDto) {
        Student student = studentRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String skillName = skillDto.getSkillName().trim();
        Skill skill = skillRepository.findByNameIgnoreCaseAndIsActiveTrue(skillName)
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setName(skillName);
                    newSkill.setCategory(skillDto.getSkillCategory());
                    return skillRepository.save(newSkill);
                });

        studentSkillRepository.findByStudentIdAndSkillIdAndSkillTypeAndIsActiveTrue(student.getId(), skill.getId(), skillDto.getSkillType())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("You already have this skill with the same type");
                });

        StudentSkill studentSkill = new StudentSkill();
        studentSkill.setStudent(student);
        studentSkill.setSkill(skill);
        studentSkill.setSkillType(skillDto.getSkillType());
        studentSkill.setSkillLevel(skillDto.getSkillLevel());
        studentSkill.setDescription(skillDto.getDescription());
        studentSkill.setActive(true);

        studentSkillRepository.save(studentSkill);
    }

    @Override
    @Transactional
    public void updateStudentSkill(Long id, String username, SkillDto skillDto) {
        Student student = studentRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentSkill studentSkill = studentSkillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student skill not found"));

        if (!studentSkill.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Not authorized to update this skill");
        }

        String skillName = skillDto.getSkillName().trim();
        Skill skill = skillRepository.findByNameIgnoreCaseAndIsActiveTrue(skillName)
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setName(skillName);
                    newSkill.setCategory(skillDto.getSkillCategory());
                    return skillRepository.save(newSkill);
                });

        studentSkillRepository.findByStudentIdAndSkillIdAndSkillTypeAndIsActiveTrue(student.getId(), skill.getId(), skillDto.getSkillType())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new IllegalArgumentException("You already have this skill with the same type");
                    }
                });

        studentSkill.setSkill(skill);
        studentSkill.setSkillType(skillDto.getSkillType());
        studentSkill.setSkillLevel(skillDto.getSkillLevel());
        studentSkill.setDescription(skillDto.getDescription());

        studentSkillRepository.save(studentSkill);
    }

    @Override
    @Transactional
    public void deleteStudentSkill(Long id, String username) {
        Student student = studentRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentSkill studentSkill = studentSkillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student skill not found"));

        if (!studentSkill.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Not authorized to delete this skill");
        }

        studentSkill.setActive(false);
        studentSkillRepository.save(studentSkill);
    }

    @Override
    public List<Skill> getAllAvailableSkills() {
        return skillRepository.findAllByIsActiveTrue();
    }

    private SkillDto mapToDto(StudentSkill studentSkill) {
        SkillDto dto = new SkillDto();
        dto.setId(studentSkill.getId());
        dto.setSkillName(studentSkill.getSkill().getName());
        dto.setSkillCategory(studentSkill.getSkill().getCategory());
        dto.setSkillType(studentSkill.getSkillType());
        dto.setSkillLevel(studentSkill.getSkillLevel());
        dto.setDescription(studentSkill.getDescription());
        return dto;
    }
}
