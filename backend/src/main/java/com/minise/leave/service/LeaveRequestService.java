package com.minise.leave.service;

import com.minise.leave.domain.LeaveRequest;
import com.minise.leave.domain.LeaveStatus;
import com.minise.leave.domain.User;
import com.minise.leave.integration.NotificationClient;
import com.minise.leave.repository.LeaveRequestRepository;
import com.minise.leave.repository.UserRepository;
import com.minise.leave.web.dto.LeaveRequestCreateDto;
import com.minise.leave.web.dto.LeaveRequestViewDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Moi luat nghiep vu nam o day, khong nam o controller. Controller chi nhan request,
 * goi xuong day, tra response. Nho vay LeaveRequestServiceTest kiem tra duoc luat ma
 * khong phai dung den web layer.
 */
@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;
    private final NotificationClient notificationClient;

    public LeaveRequestService(
            LeaveRequestRepository leaveRequestRepository,
            UserRepository userRepository,
            NotificationClient notificationClient
    ) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.userRepository = userRepository;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public LeaveRequestViewDto createRequest(String requesterEmail, LeaveRequestCreateDto dto) {
        if (dto.endDate().isBefore(dto.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate must not be before startDate");
        }
        User requester = findUserByEmail(requesterEmail);
        LeaveRequest request = new LeaveRequest(requester, dto.startDate(), dto.endDate(), dto.reason());
        return LeaveRequestViewDto.from(leaveRequestRepository.save(request));
    }

    /**
     * readOnly=true de giu Hibernate session mo den het method. Can that, vi
     * LeaveRequestViewDto.from() con doc getRequester().getEmail() ma quan he do la LAZY -
     * khong co transaction bao quanh thi session dong truoc, luc map DTO se no
     * LazyInitializationException.
     *
     * open-in-view cung che duoc loi nay nhung da tat trong application.yml: de transaction
     * mo am tham den tan view layer thi sau nay khong con biet query chay o dau nua.
     */
    @Transactional(readOnly = true)
    public List<LeaveRequestViewDto> listMine(String requesterEmail) {
        User requester = findUserByEmail(requesterEmail);
        return leaveRequestRepository.findByRequester_Id(requester.getId()).stream()
                .map(LeaveRequestViewDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveRequestViewDto> listForTeamOf(String managerEmail) {
        User manager = findUserByEmail(managerEmail);
        return leaveRequestRepository.findByRequester_ManagerId(manager.getId()).stream()
                .map(LeaveRequestViewDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveRequestViewDto> listAll(LeaveStatus statusFilter) {
        List<LeaveRequest> requests = statusFilter != null
                ? leaveRequestRepository.findByStatus(statusFilter)
                : leaveRequestRepository.findAll();
        return requests.stream().map(LeaveRequestViewDto::from).toList();
    }

    @Transactional
    public LeaveRequestViewDto decide(String managerEmail, Long requestId, LeaveStatus decision) {
        if (decision != LeaveStatus.APPROVED && decision != LeaveStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "decision must be APPROVED or REJECTED");
        }

        User manager = findUserByEmail(managerEmail);
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Leave request not found"));

        // Quan ly chi duoc duyet don cua cap duoi truc tiep. Rang buoc nay khong dua len
        // @PreAuthorize duoc, vi phai doc du lieu cua chinh don do moi biet, ma annotation
        // thi chi nhin thay role cua nguoi goi.
        Long requesterManagerId = request.getRequester().getManagerId();
        if (requesterManagerId == null || !requesterManagerId.equals(manager.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only decide on your direct reports' requests");
        }

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This request has already been decided");
        }

        request.decide(decision, manager);
        notificationClient.notifyDecision(request);
        return LeaveRequestViewDto.from(request);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
    }
}
