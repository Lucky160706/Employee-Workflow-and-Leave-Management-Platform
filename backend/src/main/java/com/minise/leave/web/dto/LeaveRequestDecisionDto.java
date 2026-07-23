package com.minise.leave.web.dto;

import com.minise.leave.domain.LeaveStatus;
import jakarta.validation.constraints.NotNull;

/** decision chi duoc phep la APPROVED hoac REJECTED - kiem tra o service layer. */
public record LeaveRequestDecisionDto(@NotNull LeaveStatus decision) {
}
