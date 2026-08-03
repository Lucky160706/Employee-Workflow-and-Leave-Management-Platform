-- Du lieu seed cho demo/hoc tap - KHONG dung cho production that.
-- Tat ca tai khoan dung chung 1 mat khau: password123 (da bam BCrypt ben duoi).
--
-- Moi INSERT deu kem "WHERE NOT EXISTS". Voi H2 thi thua, vi DB moi tinh moi lan chay.
-- Nhung Postgres trong docker-compose co volume song lau hon container, nen lan
-- `docker compose up` thu hai se chay lai file nay va dam vao UNIQUE(email), app chet
-- ngay luc khoi dong. Kieu viet nay chay dung tren ca hai, khoi phai tach hai ban seed.
INSERT INTO users (email, password_hash, full_name, role, manager_id)
SELECT 'hr@co.com', '$2a$10$WMj.CoD7gOSRbFXJ/Fn33e33C.Nr1ib.Qm70YMoz16c9c3WUHSjIW', 'Hoa (HR)', 'HR', NULL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'hr@co.com');

INSERT INTO users (email, password_hash, full_name, role, manager_id)
SELECT 'manager@co.com', '$2a$10$WMj.CoD7gOSRbFXJ/Fn33e33C.Nr1ib.Qm70YMoz16c9c3WUHSjIW', 'Minh (Manager)', 'MANAGER', NULL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'manager@co.com');

INSERT INTO users (email, password_hash, full_name, role, manager_id)
SELECT 'employee1@co.com', '$2a$10$WMj.CoD7gOSRbFXJ/Fn33e33C.Nr1ib.Qm70YMoz16c9c3WUHSjIW', 'An (Employee)',
       'EMPLOYEE', (SELECT id FROM users WHERE email = 'manager@co.com')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'employee1@co.com');

INSERT INTO users (email, password_hash, full_name, role, manager_id)
SELECT 'employee2@co.com', '$2a$10$WMj.CoD7gOSRbFXJ/Fn33e33C.Nr1ib.Qm70YMoz16c9c3WUHSjIW', 'Binh (Employee)',
       'EMPLOYEE', (SELECT id FROM users WHERE email = 'manager@co.com')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'employee2@co.com');

-- 2 don mau de video/demo co san du lieu ngay khi vua chay app.
INSERT INTO leave_requests (requester_id, start_date, end_date, reason, status, created_at)
SELECT (SELECT id FROM users WHERE email = 'employee1@co.com'), DATE '2026-08-10', DATE '2026-08-12',
       'Ve que dam gio', 'PENDING', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM leave_requests WHERE reason = 'Ve que dam gio');

INSERT INTO leave_requests (requester_id, start_date, end_date, reason, status, created_at)
SELECT (SELECT id FROM users WHERE email = 'employee2@co.com'), DATE '2026-08-01', DATE '2026-08-01',
       'Kham suc khoe dinh ky', 'PENDING', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM leave_requests WHERE reason = 'Kham suc khoe dinh ky');
