package com.skillbridge.controller;

import com.skillbridge.dto.ProfileDto;
import com.skillbridge.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final com.skillbridge.service.SkillService skillService;

    public ProfileController(ProfileService profileService, com.skillbridge.service.SkillService skillService) {
        this.profileService = profileService;
        this.skillService = skillService;
    }

    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        ProfileDto profile = profileService.getProfileByUsername(principal.getName());
        model.addAttribute("profile", profile);
        model.addAttribute("skills", skillService.getStudentSkills(principal.getName()));
        return "profile/view";
    }

    @GetMapping("/{username}")
    public String viewPublicProfile(@org.springframework.web.bind.annotation.PathVariable String username, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            ProfileDto profile = profileService.getProfileByUsername(username);
            model.addAttribute("profile", profile);
            model.addAttribute("skills", skillService.getStudentSkills(username));
            return "profile/public-view";
        } catch (Exception e) {
            return "redirect:/matches"; // redirect back if user not found
        }
    }

    @GetMapping("/edit")
    public String showEditForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        ProfileDto profile = profileService.getProfileByUsername(principal.getName());
        model.addAttribute("profileDto", profile);
        return "profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute("profileDto") ProfileDto profileDto,
                                BindingResult bindingResult,
                                Principal principal,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            // Need to re-populate read-only fields since they are not submitted in the form
            ProfileDto existingProfile = profileService.getProfileByUsername(principal.getName());
            profileDto.setUsername(existingProfile.getUsername());
            profileDto.setEmail(existingProfile.getEmail());
            profileDto.setExistingProfilePicturePath(existingProfile.getExistingProfilePicturePath());
            return "profile/edit";
        }

        try {
            profileService.updateProfile(principal.getName(), profileDto);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while updating the profile: " + e.getMessage());
            // Need to re-populate read-only fields for the form
            ProfileDto existingProfile = profileService.getProfileByUsername(principal.getName());
            profileDto.setUsername(existingProfile.getUsername());
            profileDto.setEmail(existingProfile.getEmail());
            profileDto.setExistingProfilePicturePath(existingProfile.getExistingProfilePicturePath());
            return "profile/edit";
        }

        return "redirect:/profile";
    }
}
