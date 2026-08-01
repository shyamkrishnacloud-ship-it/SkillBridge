package com.skillbridge.model.entity;

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

    @Column(length = 300)
    private String description;

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public SkillType getSkillType() { return skillType; }
    public void setSkillType(SkillType skillType) { this.skillType = skillType; }

    public SkillLevel getSkillLevel() { return skillLevel; }
    public void setSkillLevel(SkillLevel skillLevel) { this.skillLevel = skillLevel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
