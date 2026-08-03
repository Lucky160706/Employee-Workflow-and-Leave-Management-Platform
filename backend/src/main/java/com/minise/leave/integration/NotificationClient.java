package com.minise.leave.integration;

import com.minise.leave.domain.LeaveRequest;

/**
 * Cho de sau nay noi vao he thong mail hoac message queue chung cua cong ty.
 * Co lap thanh interface de doi MockNotificationClient sang ban that - JavaMailSender,
 * SES, hay day vao queue - ma khong phai dong vao LeaveRequestService.
 *
 * Neu luc thay ban that ma buoc phai sua service, nghia la interface nay dang thiet ke sai.
 */
public interface NotificationClient {
    void notifyDecision(LeaveRequest request);
}
