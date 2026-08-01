package com.skillbridge.repository;

import com.skillbridge.model.entity.StudentSkill;
import com.skillbridge.model.enums.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {
    List<StudentSkill> findByStudentIdAndSkillTypeAndIsActiveTrue(Long studentId, SkillType skillType);
    List<StudentSkill> findByStudentIdAndIsActiveTrue(Long studentId);
}
