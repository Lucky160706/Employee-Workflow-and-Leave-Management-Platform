package com.minise.leave.integration;

import com.minise.leave.domain.LeaveRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * GIOI HAN: chi log ra console chu khong gui email that, du de thay cho noi nam o dau
 * ma khong phai dung mail server cho mot bai hoc. Thay bang ban goi JavaMailSender,
 * SES hoac day message vao queue la xong, khong dong gi den service.
 */
@Component
public class MockNotificationClient implements NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(MockNotificationClient.class);

    @Override
    public void notifyDecision(LeaveRequest request) {
        log.info("[MOCK EMAIL] To: {} - Your leave request {} was {}",
                request.getRequester().getEmail(), request.getId(), request.getStatus());
    }
}
