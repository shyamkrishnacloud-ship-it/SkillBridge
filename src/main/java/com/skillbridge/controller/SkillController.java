package com.skillbridge.controller;

import com.skillbridge.dto.SkillDto;
import com.skillbridge.model.entity.Skill;
import com.skillbridge.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/profile/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/add")
    public String showAddSkillForm(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        model.addAttribute("skillDto", new SkillDto());
        return "profile/skill-form";
    }

    @PostMapping("/add")
    public String addSkill(@Valid @ModelAttribute("skillDto") SkillDto skillDto,
                           BindingResult bindingResult,
                           Principal principal,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            return "profile/skill-form";
        }

        try {
            skillService.addStudentSkill(principal.getName(), skillDto);
            redirectAttributes.addFlashAttribute("successMessage", "Skill added successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding skill: " + e.getMessage());
            return "profile/skill-form";
        }

        return "redirect:/profile";
    }

    @GetMapping("/{id}/edit")
    public String showEditSkillForm(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        try {
            SkillDto skillDto = skillService.getStudentSkill(id, principal.getName());
            model.addAttribute("skillDto", skillDto);
            return "profile/skill-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error loading skill: " + e.getMessage());
            return "redirect:/profile";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateSkill(@PathVariable Long id,
                              @Valid @ModelAttribute("skillDto") SkillDto skillDto,
                              BindingResult bindingResult,
                              Principal principal,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            return "profile/skill-form";
        }

        try {
            skillService.updateStudentSkill(id, principal.getName(), skillDto);
            redirectAttributes.addFlashAttribute("successMessage", "Skill updated successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating skill: " + e.getMessage());
            return "profile/skill-form";
        }

        return "redirect:/profile";
    }

    @PostMapping("/{id}/delete")
    public String deleteSkill(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        try {
            skillService.deleteStudentSkill(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Skill deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting skill: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}
