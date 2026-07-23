package com.minise.leave.web.dto;

import com.minise.leave.domain.LeaveRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LeaveRequestViewDto(
        Long id,
        String requesterEmail,
        String requesterFullName,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        String status,
        String decidedByEmail,
        LocalDateTime decidedAt,
        LocalDateTime createdAt
) {
    /** Chuyen entity -> DTO ngay tai noi dung, khong can mapping library cho 1 entity. */
    public static LeaveRequestViewDto from(LeaveRequest r) {
        return new LeaveRequestViewDto(
                r.getId(),
                r.getRequester().getEmail(),
                r.getRequester().getFullName(),
                r.getStartDate(),
                r.getEndDate(),
                r.getReason(),
                r.getStatus().name(),
                r.getDecidedByUser() != null ? r.getDecidedByUser().getEmail() : null,
                r.getDecidedAt(),
                r.getCreatedAt()
        );
    }
}
