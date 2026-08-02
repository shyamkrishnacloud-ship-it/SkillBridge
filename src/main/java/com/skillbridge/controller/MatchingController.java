package com.skillbridge.controller;

import com.skillbridge.model.enums.AvailabilityMode;
import com.skillbridge.model.enums.SkillCategory;
import com.skillbridge.service.MatchingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/matches")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping
    public String getMatches(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) SkillCategory category,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) AvailabilityMode availability,
            Model model,
            Principal principal) {
        
        if (principal == null) {
            return "redirect:/login";
        }

        model.addAttribute("matches", matchingService.getMatches(principal.getName(), skill, category, department, availability));
        
        // Pass filter values back to model for the UI
        model.addAttribute("paramSkill", skill);
        model.addAttribute("paramCategory", category);
        model.addAttribute("paramDepartment", department);
        model.addAttribute("paramAvailability", availability);
        
        // Pass enums for dropdowns
        model.addAttribute("categories", SkillCategory.values());
        model.addAttribute("availabilities", AvailabilityMode.values());

        return "matching/matches";
    }
}
