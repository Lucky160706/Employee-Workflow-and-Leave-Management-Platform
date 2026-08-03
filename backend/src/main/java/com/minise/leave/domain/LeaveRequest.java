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
     * De @ManyToOne chu khong de Long cho gon, vi nho vay Spring Data doc duoc xuyen
     * qua quan he: findByRequester_ManagerId(...) tu sinh ra JOIN, khoi viet tay.
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
     * Chi ghi lai quyet dinh, khong kiem tra gi ca. Ai duoc duyet va don con PENDING
     * hay khong da duoc service kiem truoc khi goi vao day.
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
