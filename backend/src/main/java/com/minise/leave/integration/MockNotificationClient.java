package com.minise.leave.integration;

import com.minise.leave.domain.LeaveRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ponytail: chi log ra console thay vi gui email that - du de minh hoa "cho noi"
 * (seam) ma khong can dung mot mail server that trong bai hoc. Nang cap: doi
 * class nay thanh 1 impl goi JavaMailSender/AWS SES hoac publish message vao
 * 1 queue (RabbitMQ/SQS) - LeaveRequestService khong doi 1 dong nao.
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
