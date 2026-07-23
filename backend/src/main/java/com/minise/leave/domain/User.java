package com.minise.leave.domain;

import jakarta.persistence.*;

/**
 * `manager_id` la self-reference (tu tro vao chinh bang users) - du de dien ta
 * "quan ly truc tiep la ai" ma khong can mo hinh cay to chuc (Department/Team)
 * phuc tap hon muc bai toan yeu cau.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** Null neu khong co quan ly truc tiep (vi du: HR). */
    @Column(name = "manager_id")
    private Long managerId;

    protected User() {
        // JPA can constructor rong
    }

    public User(String email, String passwordHash, String fullName, Role role, Long managerId) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.managerId = managerId;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public Role getRole() {
        return role;
    }

    public Long getManagerId() {
        return managerId;
    }
}
