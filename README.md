# Leave Approval System — Mini SE Project

Một hệ thống duyệt nghỉ phép nội bộ (**Leave Approval System**), xây theo đúng cách một team thật sẽ làm — nhỏ gọn, nhưng đủ chuẩn để cắm vào một hệ thống doanh nghiệp lớn hơn. Đây là project mini dành cho sinh viên → junior dev, đi kèm series 4 video giải thích tư duy đằng sau từng quyết định kỹ thuật.

## Bài toán

- **Employee** gửi đơn xin nghỉ (ngày bắt đầu, ngày kết thúc, lý do).
- **Manager** duyệt/từ chối đơn của những người báo cáo trực tiếp cho mình.
- **HR** xem toàn bộ đơn trong công ty.

Giống hệt việc học sinh xin nghỉ học và giáo viên chủ nhiệm duyệt — chỉ khác là có thêm vai trò, quyền hạn, và phải chạy được trong môi trường doanh nghiệp thật.

## 4 giai đoạn (khớp 4 video)

| Stage | Nội dung | Thư mục | Video |
|---|---|---|---|
| 1 | Thiết kế: yêu cầu, ERD, kiến trúc | `docs/` | Video 1 |
| 2 | Backend & API (Spring Boot, JWT auth) | `backend/` | Video 2 |
| 3 | Frontend & tích hợp (React) | `frontend/` | Video 3 |
| 4 | Test, Docker, CI/CD, tích hợp doanh nghiệp mô phỏng | `backend/.../integration/`, `docker/`, `.github/` | Video 4 |

## Cách chạy từng stage

**Stage 2 — chỉ backend (không cần Docker):**
```bash
cd backend
mvn spring-boot:run
```
- API: `http://localhost:8080/api/v1/...`
- Swagger UI (tài liệu API — cũng là "hợp đồng" để hệ thống khác tích hợp vào): `http://localhost:8080/swagger-ui.html`
- Health check (để load balancer/k8s biết app còn sống): `http://localhost:8080/actuator/health`
- Tài khoản demo (xem `backend/src/main/resources/data.sql`): `manager@co.com` / `employee1@co.com` / `hr@co.com`, mật khẩu `password123`

**Stage 3 — full stack:**
```bash
# terminal 1
cd backend && mvn spring-boot:run
# terminal 2
cd frontend && npm install && npm run dev
```
Mở `http://localhost:5173`, đăng nhập bằng 1 trong 3 tài khoản demo ở trên.

**Stage 4 — chạy toàn bộ qua Docker (giống môi trường thật hơn, dùng Postgres thay vì H2):**
```bash
docker compose up --build
```

## Vì sao dự án nhỏ nhưng không phải "code demo"

Mọi lựa chọn đơn giản hóa trong project này (VD: `schema.sql` thay vì Flyway, JWT không có refresh-token, không có màn đăng ký user) là quyết định có chủ đích ở quy mô nhỏ — không phải thiếu sót. Từng điểm được ghi chú `ponytail:` ngay trong code, kèm lý do và "nếu lớn hơn thì nâng cấp thế nào". Đọc file `docs/architecture.md` để biết chi tiết.

## Giới hạn đã biết (chưa cần sửa cho mini project)

`schema.sql` thay vì Flyway · JWT không refresh-token · không có đăng ký user (chỉ seed sẵn) · frontend chưa có test tự động · API list chưa phân trang · mật khẩu seed dùng chung cho demo.
