package com.minise.leave.integration;

import com.minise.leave.domain.LeaveRequest;

/**
 * "Integration seam": mot he thong doanh nghiep that luon can gui thong bao qua
 * mail server/message queue chung cua cong ty. Co lap thanh 1 interface o day
 * de sau nay thay MockNotificationClient bang impl that (JavaMailSender, SES,
 * publish vao 1 queue...) MA KHONG PHAI SUA noi goi no (LeaveRequestService).
 */
public interface NotificationClient {
    void notifyDecision(LeaveRequest request);
}
