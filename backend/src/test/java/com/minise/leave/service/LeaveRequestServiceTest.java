package com.minise.leave.service;

import com.minise.leave.domain.LeaveRequest;
import com.minise.leave.domain.LeaveStatus;
import com.minise.leave.domain.Role;
import com.minise.leave.domain.User;
import com.minise.leave.integration.NotificationClient;
import com.minise.leave.repository.LeaveRequestRepository;
import com.minise.leave.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Chi kiem hai luat quan trong nhat, cung la hai luat khong dua len @PreAuthorize duoc
 * vi phai doc du lieu cua tung don moi biet: quan ly chi duyet duoc don cua cap duoi
 * truc tiep, va don da co quyet dinh thi khong sua lai.
 *
 * Phai dung ReflectionTestUtils.setField de gan id cho entity: binh thuong JPA tu sinh
 * id luc luu vao DB, nhung o day repository bi mock nen khong co buoc luu that.
 */
@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private LeaveRequestService service;

    private User managerWithId(long id) {
        User manager = new User("manager@co.com", "hash", "Manager", Role.MANAGER, null);
        ReflectionTestUtils.setField(manager, "id", id);
        return manager;
    }

    private User employeeReportingTo(long employeeId, Long managerId) {
        User employee = new User("employee@co.com", "hash", "Employee", Role.EMPLOYEE, managerId);
        ReflectionTestUtils.setField(employee, "id", employeeId);
        return employee;
    }

    private LeaveRequest requestFrom(User requester) {
        LeaveRequest request = new LeaveRequest(requester, LocalDate.now(), LocalDate.now().plusDays(1), "Nghi phep");
        ReflectionTestUtils.setField(request, "id", 1L);
        return request;
    }

    @Test
    void managerCannotDecideOnNonDirectReport() {
        User manager = managerWithId(10L);
        User otherTeamEmployee = employeeReportingTo(2L, 99L); // bao cao cho manager khac (id=99)
        LeaveRequest request = requestFrom(otherTeamEmployee);

        when(userRepository.findByEmail("manager@co.com")).thenReturn(Optional.of(manager));
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.decide("manager@co.com", 1L, LeaveStatus.APPROVED))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("direct reports");
    }

    @Test
    void cannotDecideOnRequestThatAlreadyHasADecision() {
        User manager = managerWithId(10L);
        User directReport = employeeReportingTo(2L, 10L);
        LeaveRequest request = requestFrom(directReport);
        request.decide(LeaveStatus.APPROVED, manager); // da duyet roi

        when(userRepository.findByEmail("manager@co.com")).thenReturn(Optional.of(manager));
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.decide("manager@co.com", 1L, LeaveStatus.REJECTED))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already been decided");
    }

    @Test
    void managerCanApproveDirectReportsPendingRequest() {
        User manager = managerWithId(10L);
        User directReport = employeeReportingTo(2L, 10L);
        LeaveRequest request = requestFrom(directReport);

        when(userRepository.findByEmail("manager@co.com")).thenReturn(Optional.of(manager));
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        var result = service.decide("manager@co.com", 1L, LeaveStatus.APPROVED);

        assertThat(result.status()).isEqualTo("APPROVED");
    }
}
