package com.skillbridge.dto;

import com.skillbridge.model.enums.SkillLevel;
import com.skillbridge.model.enums.SkillType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SkillDto {
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "Skill name is required")
    private String skillName;
    
    @NotNull(message = "Skill category is required")
    private com.skillbridge.model.enums.SkillCategory skillCategory;

    @NotNull(message = "Skill type is required")
    private SkillType skillType;

    @NotNull(message = "Skill level is required")
    private SkillLevel skillLevel;

    @Size(max = 300, message = "Description must not exceed 300 characters")
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public com.skillbridge.model.enums.SkillCategory getSkillCategory() { return skillCategory; }
    public void setSkillCategory(com.skillbridge.model.enums.SkillCategory skillCategory) { this.skillCategory = skillCategory; }

    public SkillType getSkillType() { return skillType; }
    public void setSkillType(SkillType skillType) { this.skillType = skillType; }

    public SkillLevel getSkillLevel() { return skillLevel; }
    public void setSkillLevel(SkillLevel skillLevel) { this.skillLevel = skillLevel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
