package com.skillbridge.service;

import com.skillbridge.dto.MatchDto;
import com.skillbridge.model.enums.AvailabilityMode;
import com.skillbridge.model.enums.SkillCategory;
import java.util.List;

public interface MatchingService {
    List<MatchDto> getMatches(String currentUsername, String skillName, SkillCategory category, String department, AvailabilityMode availability);
}
