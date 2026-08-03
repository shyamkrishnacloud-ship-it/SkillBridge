package com.skillbridge.service.impl;

import com.skillbridge.dto.SwapRequestCreationDto;
import com.skillbridge.dto.SwapRequestDto;
import com.skillbridge.model.entity.Student;
import com.skillbridge.model.entity.StudentSkill;
import com.skillbridge.model.entity.SwapRequest;
import com.skillbridge.model.enums.RequestStatus;
import com.skillbridge.repository.StudentRepository;
import com.skillbridge.repository.StudentSkillRepository;
import com.skillbridge.repository.SwapRequestRepository;
import com.skillbridge.service.SwapRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SwapRequestServiceImpl implements SwapRequestService {

    private final SwapRequestRepository swapRequestRepository;
    private final StudentRepository studentRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final com.skillbridge.repository.ReviewRepository reviewRepository;

    public SwapRequestServiceImpl(SwapRequestRepository swapRequestRepository,
                                  StudentRepository studentRepository,
                                  StudentSkillRepository studentSkillRepository,
                                  com.skillbridge.repository.ReviewRepository reviewRepository) {
        this.swapRequestRepository = swapRequestRepository;
        this.studentRepository = studentRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void sendSwapRequest(String senderUsername, SwapRequestCreationDto creationDto) {
        if (senderUsername.equals(creationDto.getReceiverUsername())) {
            throw new IllegalArgumentException("Cannot send a swap request to yourself.");
        }

        Student sender = studentRepository.findByUsernameAndIsActiveTrue(senderUsername)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found or inactive"));

        Student receiver = studentRepository.findByUsernameAndIsActiveTrue(creationDto.getReceiverUsername())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found or inactive"));

        if (hasActiveSwapRequest(senderUsername, receiver.getUsername())) {
            throw new IllegalArgumentException("You already have an active swap request with this user.");
        }

        StudentSkill offeredSkill = studentSkillRepository.findById(creationDto.getOfferedSkillId())
                .filter(com.skillbridge.model.entity.BaseEntity::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Offered skill not found or inactive"));

        if (!offeredSkill.getStudent().getId().equals(sender.getId())) {
            throw new IllegalArgumentException("Offered skill does not belong to sender.");
        }

        StudentSkill requestedSkill = studentSkillRepository.findById(creationDto.getRequestedSkillId())
                .filter(com.skillbridge.model.entity.BaseEntity::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Requested skill not found or inactive"));

        if (!requestedSkill.getStudent().getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Requested skill does not belong to receiver.");
        }

        boolean exists = swapRequestRepository.existsByRequesterAndReceiverAndOfferedSkillAndRequestedSkillAndStatusAndIsActiveTrue(
                sender, receiver, offeredSkill, requestedSkill, RequestStatus.PENDING
        );
        if (exists) {
            throw new IllegalArgumentException("A pending swap request already exists for these skills.");
        }

        SwapRequest request = new SwapRequest();
        request.setRequester(sender);
        request.setReceiver(receiver);
        request.setOfferedSkill(offeredSkill);
        request.setRequestedSkill(requestedSkill);
        request.setStatus(RequestStatus.PENDING);
        request.setMessage(creationDto.getMessage());
        swapRequestRepository.save(request);
    }

    @Override
    public void acceptRequest(Long requestId, String username) {
        SwapRequest request = getActiveRequestById(requestId);
        if (!request.getReceiver().getUsername().equals(username)) {
            throw new SecurityException("Unauthorized to accept this request");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be accepted.");
        }
        request.setStatus(RequestStatus.ACCEPTED);
        swapRequestRepository.save(request);
    }

    @Override
    public void rejectRequest(Long requestId, String username) {
        SwapRequest request = getActiveRequestById(requestId);
        if (!request.getReceiver().getUsername().equals(username)) {
            throw new SecurityException("Unauthorized to reject this request");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be rejected.");
        }
        request.setStatus(RequestStatus.REJECTED);
        swapRequestRepository.save(request);
    }

    @Override
    public void cancelRequest(Long requestId, String username) {
        SwapRequest request = getActiveRequestById(requestId);
        if (!request.getRequester().getUsername().equals(username)) {
            throw new SecurityException("Unauthorized to cancel this request");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be canceled.");
        }
        request.setStatus(RequestStatus.CANCELED);
        swapRequestRepository.save(request);
    }

    @Override
    public void completeRequest(Long requestId, String username) {
        SwapRequest request = getActiveRequestById(requestId);
        if (!request.getRequester().getUsername().equals(username) && !request.getReceiver().getUsername().equals(username)) {
            throw new SecurityException("Unauthorized to complete this request");
        }
        if (request.getStatus() != RequestStatus.ACCEPTED) {
            throw new IllegalStateException("Only accepted requests can be marked as completed.");
        }

        if (request.getRequester().getUsername().equals(username)) {
            request.setCompletedByRequester(true);
        } else {
            request.setCompletedByReceiver(true);
        }

        if (request.isCompletedByRequester() && request.isCompletedByReceiver()) {
            request.setStatus(RequestStatus.COMPLETED);
        }

        swapRequestRepository.save(request);
    }

    @Override
    public List<SwapRequestDto> getIncomingRequests(String username) {
        Student user = studentRepository.findByUsernameAndIsActiveTrue(username).orElse(null);
        Long userId = user != null ? user.getId() : -1L;
        return swapRequestRepository.findByReceiver_UsernameAndIsActiveTrueOrderByCreatedAtDesc(username)
                .stream().map(req -> mapToDto(req, userId)).collect(Collectors.toList());
    }

    @Override
    public List<SwapRequestDto> getOutgoingRequests(String username) {
        Student user = studentRepository.findByUsernameAndIsActiveTrue(username).orElse(null);
        Long userId = user != null ? user.getId() : -1L;
        return swapRequestRepository.findByRequester_UsernameAndIsActiveTrueOrderByCreatedAtDesc(username)
                .stream().map(req -> mapToDto(req, userId)).collect(Collectors.toList());
    }

    @Override
    public boolean hasActiveSwapRequest(String username1, String username2) {
        Student user1 = studentRepository.findByUsernameAndIsActiveTrue(username1).orElse(null);
        Student user2 = studentRepository.findByUsernameAndIsActiveTrue(username2).orElse(null);
        
        if (user1 == null || user2 == null) {
            return false;
        }

        return swapRequestRepository.existsActiveRequestBetweenUsers(
                user1.getId(), 
                user2.getId(), 
                java.util.Arrays.asList(RequestStatus.PENDING, RequestStatus.ACCEPTED)
        );
    }

    private SwapRequest getActiveRequestById(Long requestId) {
        return swapRequestRepository.findById(requestId)
                .filter(com.skillbridge.model.entity.BaseEntity::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
    }

    private SwapRequestDto mapToDto(SwapRequest request, Long currentUserId) {
        SwapRequestDto dto = new SwapRequestDto();
        dto.setId(request.getId());
        dto.setSenderUsername(request.getRequester().getUsername());
        dto.setReceiverUsername(request.getReceiver().getUsername());
        dto.setOfferedSkillId(request.getOfferedSkill().getId());
        dto.setRequestedSkillId(request.getRequestedSkill().getId());
        dto.setOfferedSkillName(request.getOfferedSkill().getSkill().getName());
        dto.setRequestedSkillName(request.getRequestedSkill().getSkill().getName());
        dto.setStatus(request.getStatus());
        dto.setCreatedDate(request.getCreatedAt());
        dto.setMessage(request.getMessage());
        
        if (request.getStatus() == RequestStatus.COMPLETED && currentUserId != -1L) {
            dto.setHasReviewed(reviewRepository.existsByReviewerIdAndSwapRequestIdAndIsActiveTrue(currentUserId, request.getId()));
        } else {
            dto.setHasReviewed(false);
        }
        
        return dto;
    }
}
