package com.minise.leave.web;

import com.minise.leave.domain.LeaveStatus;
import com.minise.leave.web.dto.LeaveRequestCreateDto;
import com.minise.leave.web.dto.LeaveRequestDecisionDto;
import com.minise.leave.web.dto.LeaveRequestViewDto;
import com.minise.leave.web.dto.LoginRequest;
import com.minise.leave.web.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test tich hop that su (khong mock security filter chain) - dang nhap that qua
 * HTTP, lay JWT that, roi dung token do goi cac endpoint khac. Day la cach kiem tra
 * gia tri cao nhat cho 1 app nho: xac nhan security filter chain + JPA + controller
 * hoat dong DUNG VOI NHAU, khong chi tung phan rieng le.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LeaveRequestControllerIT {

    @Autowired
    private TestRestTemplate rest;

    private String loginAndGetToken(String email, String password) {
        var response = rest.postForEntity("/api/v1/auth/login", new LoginRequest(email, password), LoginResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody().token();
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    void employeeCreatesRequestAndManagerApprovesIt() {
        String employeeToken = loginAndGetToken("employee1@co.com", "password123");

        var createDto = new LeaveRequestCreateDto(
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(6), "Nghi kham benh");
        var createResponse = rest.exchange(
                "/api/v1/leave-requests",
                HttpMethod.POST,
                new HttpEntity<>(createDto, authHeaders(employeeToken)),
                LeaveRequestViewDto.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody().status()).isEqualTo("PENDING");
        Long requestId = createResponse.getBody().id();

        String managerToken = loginAndGetToken("manager@co.com", "password123");
        var decideResponse = rest.exchange(
                "/api/v1/leave-requests/" + requestId + "/decision",
                HttpMethod.PATCH,
                new HttpEntity<>(new LeaveRequestDecisionDto(LeaveStatus.APPROVED), authHeaders(managerToken)),
                LeaveRequestViewDto.class);

        assertThat(decideResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(decideResponse.getBody().status()).isEqualTo("APPROVED");
    }

    /**
     * Token rac/het han la chuyen BINH THUONG (token song 8 tieng roi het han), khong phai
     * loi server. Truoc day JwtAuthFilter de exception cua thu vien JWT thoat ra ngoai nen
     * Spring tra ve 500 - frontend se bao "loi he thong" thay vi day nguoi dung ve man
     * dang nhap. Test nay khoa lai hanh vi dung: 401.
     */
    @Test
    void malformedTokenIsRejectedAsUnauthorizedNotServerError() {
        var response = rest.exchange(
                "/api/v1/leave-requests/mine",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders("this.is.not.a.jwt")),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void employeeCannotAccessManagerTeamEndpoint() {
        String employeeToken = loginAndGetToken("employee1@co.com", "password123");

        var response = rest.exchange(
                "/api/v1/leave-requests/team",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(employeeToken)),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
