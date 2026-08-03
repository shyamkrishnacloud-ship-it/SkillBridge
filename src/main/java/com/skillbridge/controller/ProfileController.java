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

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Arrays;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final com.skillbridge.service.SkillService skillService;
    private final com.skillbridge.service.SwapRequestService swapRequestService;
    private final com.skillbridge.service.ReviewService reviewService;

    public ProfileController(ProfileService profileService, 
                             com.skillbridge.service.SkillService skillService,
                             com.skillbridge.service.SwapRequestService swapRequestService,
                             com.skillbridge.service.ReviewService reviewService) {
        this.profileService = profileService;
        this.skillService = skillService;
        this.swapRequestService = swapRequestService;
        this.reviewService = reviewService;
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
    public String viewPublicProfile(@org.springframework.web.bind.annotation.PathVariable String username, Model model,
            Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            ProfileDto profile = profileService.getProfileByUsername(username);
            model.addAttribute("profile", profile);
            model.addAttribute("skills", skillService.getStudentSkills(username));
            model.addAttribute("isSelf", principal.getName().equals(username));
            
            if (!principal.getName().equals(username)) {
                model.addAttribute("hasActiveRequest", swapRequestService.hasActiveSwapRequest(principal.getName(), username));
            } else {
                model.addAttribute("hasActiveRequest", false);
            }
            
            java.util.List<com.skillbridge.dto.ReviewDto> allReviews = reviewService.getReviewsForUser(username);
            model.addAttribute("recentReviews", allReviews.size() > 5 ? allReviews.subList(0, 5) : allReviews);
            
            return "profile/public-view";
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
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

    @GetMapping("/partner/{username}")
    public String viewPartnerDetails(@org.springframework.web.bind.annotation.PathVariable String username, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String currentUsername = principal.getName();
        if (currentUsername.equals(username)) {
            return "redirect:/profile";
        }
        
        // Strict Relationship Check
        boolean hasRelationship = swapRequestService.hasRelationship(currentUsername, username);
        
        if (!hasRelationship) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied: You do not have an accepted or completed swap request with this user.");
        }
        
        ProfileDto partnerProfile = profileService.getProfileByUsername(username);
        model.addAttribute("partner", partnerProfile);
        
        return "profile/partner-details";
    }
}
