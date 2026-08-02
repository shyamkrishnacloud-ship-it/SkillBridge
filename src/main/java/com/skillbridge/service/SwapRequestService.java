package com.skillbridge.service;

import com.skillbridge.dto.SwapRequestCreationDto;
import com.skillbridge.dto.SwapRequestDto;
import java.util.List;

public interface SwapRequestService {
    void sendSwapRequest(String senderUsername, SwapRequestCreationDto creationDto);
    void acceptRequest(Long requestId, String username);
    void rejectRequest(Long requestId, String username);
    void cancelRequest(Long requestId, String username);
    void completeRequest(Long requestId, String username);
    List<SwapRequestDto> getIncomingRequests(String username);
    List<SwapRequestDto> getOutgoingRequests(String username);
    boolean hasActiveSwapRequest(String user1, String user2);
}
