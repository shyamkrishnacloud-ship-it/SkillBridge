package com.skillbridge.service;

import com.skillbridge.dto.SkillDto;
import com.skillbridge.model.entity.Skill;
import java.util.List;

public interface SkillService {
    List<SkillDto> getStudentSkills(String username);
    SkillDto getStudentSkill(Long id, String username);
    void addStudentSkill(String username, SkillDto skillDto);
    void updateStudentSkill(Long id, String username, SkillDto skillDto);
    void deleteStudentSkill(Long id, String username);
    List<Skill> getAllAvailableSkills();
}
