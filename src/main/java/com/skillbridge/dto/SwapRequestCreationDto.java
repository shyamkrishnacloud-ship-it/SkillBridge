package com.skillbridge.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SwapRequestCreationDto {

    @NotNull(message = "Receiver is required")
    private String receiverUsername;

    @NotNull(message = "Offered skill is required")
    private Long offeredSkillId;

    @NotNull(message = "Requested skill is required")
    private Long requestedSkillId;

    @Size(max = 500, message = "Message must not exceed 500 characters")
    private String message;

    // Getters and Setters
    public String getReceiverUsername() { return receiverUsername; }
    public void setReceiverUsername(String receiverUsername) { this.receiverUsername = receiverUsername; }

    public Long getOfferedSkillId() { return offeredSkillId; }
    public void setOfferedSkillId(Long offeredSkillId) { this.offeredSkillId = offeredSkillId; }

    public Long getRequestedSkillId() { return requestedSkillId; }
    public void setRequestedSkillId(Long requestedSkillId) { this.requestedSkillId = requestedSkillId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
