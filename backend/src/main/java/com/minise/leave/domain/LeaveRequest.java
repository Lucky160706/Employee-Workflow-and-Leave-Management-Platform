package com.minise.leave.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @ManyToOne (khong phai plain Long FK) de Spring Data JPA co the query truc tiep
     * qua quan he, vi du findByRequester_ManagerId(...) - day la cach idiomatic hon
     * la tu viet JOIN bang tay.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status;

    /** Quan ly nao da duyet - null neu con PENDING. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by")
    private User decidedByUser;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected LeaveRequest() {
        // JPA can constructor rong
    }

    public LeaveRequest(User requester, LocalDate startDate, LocalDate endDate, String reason) {
        this.requester = requester;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = LeaveStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Ghi nhan quyet dinh duyet/tu choi. Cac rang buoc nghiep vu (ai duoc duyet,
     * don co con PENDING khong) duoc kiem tra o service layer truoc khi goi ham nay -
     * entity chi chiu trach nhiem luu dung trang thai, khong tu kiem tra quyen.
     */
    public void decide(LeaveStatus decision, User decider) {
        this.status = decision;
        this.decidedByUser = decider;
        this.decidedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getRequester() {
        return requester;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public User getDecidedByUser() {
        return decidedByUser;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
