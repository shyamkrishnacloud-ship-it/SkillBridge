package com.skillbridge.model.entity;

import com.skillbridge.model.enums.RequestStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "swap_requests", indexes = {
    @Index(name = "idx_swap_requester", columnList = "requester_id"),
    @Index(name = "idx_swap_receiver", columnList = "receiver_id"),
    @Index(name = "idx_swap_status", columnList = "status")
})
public class SwapRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Student requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Student receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offered_skill_id", nullable = false)
    private StudentSkill offeredSkill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_skill_id", nullable = false)
    private StudentSkill requestedSkill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    private boolean completedByRequester = false;
    private boolean completedByReceiver = false;

    public Student getRequester() { return requester; }
    public void setRequester(Student requester) { this.requester = requester; }

    public Student getReceiver() { return receiver; }
    public void setReceiver(Student receiver) { this.receiver = receiver; }

    public StudentSkill getOfferedSkill() { return offeredSkill; }
    public void setOfferedSkill(StudentSkill offeredSkill) { this.offeredSkill = offeredSkill; }

    public StudentSkill getRequestedSkill() { return requestedSkill; }
    public void setRequestedSkill(StudentSkill requestedSkill) { this.requestedSkill = requestedSkill; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public boolean isCompletedByRequester() { return completedByRequester; }
    public void setCompletedByRequester(boolean completedByRequester) { this.completedByRequester = completedByRequester; }

    public boolean isCompletedByReceiver() { return completedByReceiver; }
    public void setCompletedByReceiver(boolean completedByReceiver) { this.completedByReceiver = completedByReceiver; }
}
