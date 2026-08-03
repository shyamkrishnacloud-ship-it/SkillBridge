package com.skillbridge.dto;

import com.skillbridge.model.enums.RequestStatus;
import java.time.LocalDateTime;

public class SwapRequestDto {
    private Long id;
    private String senderUsername;
    private String receiverUsername;
    private Long offeredSkillId;
    private Long requestedSkillId;
    private String offeredSkillName;
    private String requestedSkillName;
    private String message;
    private RequestStatus status;
    private LocalDateTime createdDate;
    private boolean hasReviewed;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getReceiverUsername() { return receiverUsername; }
    public void setReceiverUsername(String receiverUsername) { this.receiverUsername = receiverUsername; }

    public Long getOfferedSkillId() { return offeredSkillId; }
    public void setOfferedSkillId(Long offeredSkillId) { this.offeredSkillId = offeredSkillId; }

    public Long getRequestedSkillId() { return requestedSkillId; }
    public void setRequestedSkillId(Long requestedSkillId) { this.requestedSkillId = requestedSkillId; }

    public String getOfferedSkillName() { return offeredSkillName; }
    public void setOfferedSkillName(String offeredSkillName) { this.offeredSkillName = offeredSkillName; }

    public String getRequestedSkillName() { return requestedSkillName; }
    public void setRequestedSkillName(String requestedSkillName) { this.requestedSkillName = requestedSkillName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public boolean isHasReviewed() { return hasReviewed; }
    public void setHasReviewed(boolean hasReviewed) { this.hasReviewed = hasReviewed; }
}
