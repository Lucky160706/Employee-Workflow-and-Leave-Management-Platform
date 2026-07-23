-- Du lieu seed cho demo/hoc tap - KHONG dung cho production that.
-- Tat ca tai khoan dung chung 1 mat khau: password123 (da bam BCrypt ben duoi).
INSERT INTO users (email, password_hash, full_name, role, manager_id) VALUES
    ('hr@co.com', '$2a$10$WMj.CoD7gOSRbFXJ/Fn33e33C.Nr1ib.Qm70YMoz16c9c3WUHSjIW', 'Hoa (HR)', 'HR', NULL);

INSERT INTO users (email, password_hash, full_name, role, manager_id) VALUES
    ('manager@co.com', '$2a$10$WMj.CoD7gOSRbFXJ/Fn33e33C.Nr1ib.Qm70YMoz16c9c3WUHSjIW', 'Minh (Manager)', 'MANAGER', NULL);

INSERT INTO users (email, password_hash, full_name, role, manager_id) VALUES
    ('employee1@co.com', '$2a$10$WMj.CoD7gOSRbFXJ/Fn33e33C.Nr1ib.Qm70YMoz16c9c3WUHSjIW', 'An (Employee)',
     'EMPLOYEE', (SELECT id FROM users WHERE email = 'manager@co.com'));

INSERT INTO users (email, password_hash, full_name, role, manager_id) VALUES
    ('employee2@co.com', '$2a$10$WMj.CoD7gOSRbFXJ/Fn33e33C.Nr1ib.Qm70YMoz16c9c3WUHSjIW', 'Binh (Employee)',
     'EMPLOYEE', (SELECT id FROM users WHERE email = 'manager@co.com'));

-- 2 don mau de video/demo co san du lieu ngay khi vua chay app.
INSERT INTO leave_requests (requester_id, start_date, end_date, reason, status, created_at) VALUES
    ((SELECT id FROM users WHERE email = 'employee1@co.com'), '2026-08-10', '2026-08-12',
     'Ve que dam gio', 'PENDING', CURRENT_TIMESTAMP);

INSERT INTO leave_requests (requester_id, start_date, end_date, reason, status, created_at) VALUES
    ((SELECT id FROM users WHERE email = 'employee2@co.com'), '2026-08-01', '2026-08-01',
     'Kham suc khoe dinh ky', 'PENDING', CURRENT_TIMESTAMP);
