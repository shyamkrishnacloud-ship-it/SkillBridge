package com.skillbridge.controller;

import com.skillbridge.dto.SkillDto;
import com.skillbridge.dto.SwapRequestCreationDto;
import com.skillbridge.dto.SwapRequestDto;
import com.skillbridge.model.enums.SkillType;
import com.skillbridge.service.SkillService;
import com.skillbridge.service.SwapRequestService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/requests")
public class SwapRequestController {

    private final SwapRequestService swapRequestService;
    private final SkillService skillService;

    public SwapRequestController(SwapRequestService swapRequestService, SkillService skillService) {
        this.swapRequestService = swapRequestService;
        this.skillService = skillService;
    }

    @GetMapping
    public String requestsRedirect() {
        return "redirect:/requests/incoming";
    }

    @GetMapping("/incoming")
    public String viewIncomingRequests(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        List<SwapRequestDto> requests = swapRequestService.getIncomingRequests(principal.getName());
        model.addAttribute("requests", requests);
        return "requests/incoming";
    }

    @GetMapping("/outgoing")
    public String viewOutgoingRequests(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        List<SwapRequestDto> requests = swapRequestService.getOutgoingRequests(principal.getName());
        model.addAttribute("requests", requests);
        return "requests/outgoing";
    }

    @GetMapping("/send/{username}")
    public String showSendRequestForm(@PathVariable String username, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        if (principal.getName().equals(username)) {
            return "redirect:/matches"; // Can't send to self
        }

        List<SkillDto> mySkills = skillService.getStudentSkills(principal.getName());
        List<SkillDto> receiverSkills = skillService.getStudentSkills(username);

        List<SkillDto> myOfferedSkills = mySkills.stream()
                .filter(s -> s.getSkillType() == SkillType.OFFERED)
                .collect(Collectors.toList());

        List<SkillDto> receiverRequiredSkills = receiverSkills.stream()
                .filter(s -> s.getSkillType() == SkillType.REQUIRED)
                .collect(Collectors.toList());

        SwapRequestCreationDto creationDto = new SwapRequestCreationDto();
        creationDto.setReceiverUsername(username);

        model.addAttribute("creationDto", creationDto);
        model.addAttribute("myOfferedSkills", myOfferedSkills);
        model.addAttribute("receiverRequiredSkills", receiverRequiredSkills);
        model.addAttribute("receiverUsername", username);

        return "requests/send";
    }

    @PostMapping("/send")
    public String sendRequest(@Valid @ModelAttribute("creationDto") SwapRequestCreationDto creationDto,
                              BindingResult bindingResult,
                              Principal principal,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (principal == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            return prepareSendFormWithError(creationDto, principal.getName(), model);
        }

        try {
            swapRequestService.sendSwapRequest(principal.getName(), creationDto);
            redirectAttributes.addFlashAttribute("successMessage", "Swap request sent successfully!");
            return "redirect:/requests/outgoing";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/requests/send/" + creationDto.getReceiverUsername();
        }
    }

    private String prepareSendFormWithError(SwapRequestCreationDto creationDto, String senderUsername, Model model) {
        List<SkillDto> mySkills = skillService.getStudentSkills(senderUsername);
        List<SkillDto> receiverSkills = skillService.getStudentSkills(creationDto.getReceiverUsername());

        List<SkillDto> myOfferedSkills = mySkills.stream()
                .filter(s -> s.getSkillType() == SkillType.OFFERED)
                .collect(Collectors.toList());

        List<SkillDto> receiverRequiredSkills = receiverSkills.stream()
                .filter(s -> s.getSkillType() == SkillType.REQUIRED)
                .collect(Collectors.toList());

        model.addAttribute("myOfferedSkills", myOfferedSkills);
        model.addAttribute("receiverRequiredSkills", receiverRequiredSkills);
        model.addAttribute("receiverUsername", creationDto.getReceiverUsername());
        
        return "requests/send";
    }

    @PostMapping("/{id}/accept")
    public String acceptRequest(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            swapRequestService.acceptRequest(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Request accepted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/requests/incoming";
    }

    @PostMapping("/{id}/reject")
    public String rejectRequest(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            swapRequestService.rejectRequest(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Request rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/requests/incoming";
    }

    @PostMapping("/{id}/cancel")
    public String cancelRequest(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            swapRequestService.cancelRequest(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Request canceled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/requests/outgoing";
    }

    @PostMapping("/{id}/complete")
    public String completeRequest(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes, @RequestParam(required = false, defaultValue = "incoming") String source) {
        if (principal == null) return "redirect:/login";
        try {
            swapRequestService.completeRequest(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Marked as completed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/requests/" + source;
    }
}
