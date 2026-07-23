package com.minise.leave.service;

import com.minise.leave.domain.LeaveRequest;
import com.minise.leave.domain.LeaveStatus;
import com.minise.leave.domain.User;
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
 * Toan bo "luat choi" nghiep vu (ai duoc lam gi) nam o day, khong nam o controller.
 * Controller chi lam nhiem vu "nhan request, goi service, tra response" - tach lop
 * nay giup logic nghiep vu test duoc doc lap voi web layer (xem LeaveRequestServiceTest).
 */
@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository, UserRepository userRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.userRepository = userRepository;
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
     * readOnly=true giu Hibernate session mo den het method - can thiet vi
     * LeaveRequestViewDto.from() doc r.getRequester().getEmail() (quan he @ManyToOne LAZY);
     * khong co transaction bao quanh, session da dong truoc khi map DTO se nem
     * LazyInitializationException. (open-in-view co the "chua" loi nay nhung ta tat no
     * o application.yml vi day la anti-pattern that su cho enterprise app - de transaction
     * ro rang trong service layer.)
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

        // Ownership check: quan ly chi duoc duyet don cua nguoi bao cao TRUC TIEP cho minh -
        // khong the dien ta rang buoc nay bang @PreAuthorize (can biet du lieu cua request cu the),
        // nen kiem tra ngay trong service.
        Long requesterManagerId = request.getRequester().getManagerId();
        if (requesterManagerId == null || !requesterManagerId.equals(manager.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only decide on your direct reports' requests");
        }

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This request has already been decided");
        }

        request.decide(decision, manager);
        return LeaveRequestViewDto.from(request);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
    }
}
