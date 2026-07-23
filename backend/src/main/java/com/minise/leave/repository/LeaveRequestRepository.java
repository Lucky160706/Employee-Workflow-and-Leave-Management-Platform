package com.minise.leave.repository;

import com.minise.leave.domain.LeaveRequest;
import com.minise.leave.domain.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByRequester_Id(Long requesterId);

    /** Don cua nhung nguoi co manager_id = managerId (doi truc tiep cua 1 quan ly). */
    List<LeaveRequest> findByRequester_ManagerId(Long managerId);

    List<LeaveRequest> findByStatus(LeaveStatus status);
}
