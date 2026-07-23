# ERD — Entity Relationship Diagram

`ERD` là sơ đồ cho biết hệ thống có những bảng dữ liệu nào và chúng liên hệ với nhau ra sao. Hệ thống này chỉ cần **2 bảng**.

```mermaid
erDiagram
    USERS ||--o{ USERS : "manager_id (tự tham chiếu)"
    USERS ||--o{ LEAVE_REQUESTS : "requester_id"
    USERS ||--o{ LEAVE_REQUESTS : "decided_by"

    USERS {
        bigint id PK
        varchar email UK "dùng để đăng nhập"
        varchar password_hash "BCrypt, không lưu plain text"
        varchar full_name
        varchar role "EMPLOYEE / MANAGER / HR"
        bigint manager_id FK "null nếu không có quản lý trực tiếp (VD: HR)"
    }

    LEAVE_REQUESTS {
        bigint id PK
        bigint requester_id FK "ai gửi đơn"
        date start_date
        date end_date
        varchar reason
        varchar status "PENDING / APPROVED / REJECTED"
        bigint decided_by FK "quản lý nào đã duyệt, null nếu còn PENDING"
        timestamp decided_at "null nếu còn PENDING"
        timestamp created_at
    }
```

## Vì sao chỉ 2 bảng (không có bảng `roles`, `departments`)

- `role` là enum cố định (3 giá trị, gần như không đổi theo thời gian) → lưu thẳng dạng `varchar`, không cần bảng lookup riêng. Nếu sau này số role tăng lên và cần gán nhiều role/thay đổi linh hoạt qua UI admin, đó là lúc tách bảng `roles` — hiện tại chưa cần.
- "Quản lý trực tiếp" là quan hệ 1 cấp, dùng cột `manager_id` tự trỏ vào chính bảng `users` là đủ diễn tả — không cần mô hình cây tổ chức (`Department`/`Team`) phức tạp hơn mức bài toán yêu cầu.
