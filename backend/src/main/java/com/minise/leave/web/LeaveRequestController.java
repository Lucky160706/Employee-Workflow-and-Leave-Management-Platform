package com.minise.leave.web;

import com.minise.leave.domain.LeaveStatus;
import com.minise.leave.service.LeaveRequestService;
import com.minise.leave.web.dto.LeaveRequestCreateDto;
import com.minise.leave.web.dto.LeaveRequestDecisionDto;
import com.minise.leave.web.dto.LeaveRequestViewDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequestViewDto create(Authentication auth, @Valid @RequestBody LeaveRequestCreateDto dto) {
        return leaveRequestService.createRequest(auth.getName(), dto);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<LeaveRequestViewDto> mine(Authentication auth) {
        return leaveRequestService.listMine(auth.getName());
    }

    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public List<LeaveRequestViewDto> team(Authentication auth) {
        return leaveRequestService.listForTeamOf(auth.getName());
    }

    @PatchMapping("/{id}/decision")
    @PreAuthorize("hasRole('MANAGER')")
    public LeaveRequestViewDto decide(
            Authentication auth,
            @PathVariable Long id,
            @Valid @RequestBody LeaveRequestDecisionDto dto
    ) {
        return leaveRequestService.decide(auth.getName(), id, dto.decision());
    }

    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public List<LeaveRequestViewDto> all(@RequestParam(required = false) LeaveStatus status) {
        return leaveRequestService.listAll(status);
    }
}
