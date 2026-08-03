package com.skillbridge.controller;

import com.skillbridge.dto.ReviewCreationDto;
import com.skillbridge.dto.ReviewDto;
import com.skillbridge.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews/new/{swapId}")
    public String showReviewForm(@PathVariable Long swapId, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        if (reviewService.hasReviewed(principal.getName(), swapId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already reviewed this swap request.");
            return "redirect:/requests/incoming";
        }

        ReviewCreationDto dto = new ReviewCreationDto();
        dto.setSwapRequestId(swapId);
        model.addAttribute("reviewDto", dto);
        return "review/form";
    }

    @PostMapping("/reviews/new/{swapId}")
    public String submitReview(@PathVariable Long swapId,
                               @Valid @ModelAttribute("reviewDto") ReviewCreationDto reviewDto,
                               BindingResult bindingResult,
                               Principal principal,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (principal == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            // Ensure the form action URL is correct on re-render
            reviewDto.setSwapRequestId(swapId);
            return "review/form";
        }

        try {
            reviewDto.setSwapRequestId(swapId);
            reviewService.submitReview(principal.getName(), reviewDto);
            redirectAttributes.addFlashAttribute("successMessage", "Review submitted successfully!");
            return "redirect:/requests/incoming";
        } catch (Exception e) {
            reviewDto.setSwapRequestId(swapId);
            model.addAttribute("errorMessage", e.getMessage());
            return "review/form";
        }
    }

    @GetMapping("/profile/{username}/reviews")
    public String viewUserReviews(@PathVariable String username, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        List<ReviewDto> reviews = reviewService.getReviewsForUser(username);
        model.addAttribute("reviews", reviews);
        model.addAttribute("revieweeUsername", username);
        model.addAttribute("averageRating", reviewService.getAverageRating(username));
        model.addAttribute("reviewCount", reviewService.getReviewCount(username));

        return "review/list";
    }
}
