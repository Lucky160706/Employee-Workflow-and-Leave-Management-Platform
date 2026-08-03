# Yêu cầu nghiệp vụ - Leave Approval System

## Actor (người dùng hệ thống)

| Actor | Vai trò (`role` trong hệ thống) | Được làm gì |
|---|---|---|
| Nhân viên | `EMPLOYEE` | Gửi đơn nghỉ phép, xem lịch sử đơn của chính mình |
| Quản lý trực tiếp | `MANAGER` | Xem đơn của những nhân viên báo cáo trực tiếp cho mình, duyệt/từ chối |
| Nhân sự (HR) | `HR` | Xem toàn bộ đơn trong công ty (để thống kê, đối chiếu) |

Mỗi user có đúng 1 role. Quan hệ "ai báo cáo cho ai" được lưu bằng 1 cột `manager_id` trên chính bảng `users` (self-reference) - không cần bảng `Department`/`Team` riêng vì bài toán chỉ cần biết "quản lý trực tiếp là ai", không cần cây tổ chức nhiều tầng.

## Luồng nghiệp vụ chính

1. Nhân viên đăng nhập → gửi đơn nghỉ phép (ngày bắt đầu, ngày kết thúc, lý do) → đơn ở trạng thái `PENDING`.
2. Quản lý đăng nhập → thấy danh sách đơn `PENDING` của đội mình → duyệt (`APPROVED`) hoặc từ chối (`REJECTED`).
   - Quản lý **chỉ** được duyệt đơn của người báo cáo trực tiếp cho mình (không được duyệt đơn của nhân viên team khác).
   - Một đơn **chỉ** được quyết định một lần - đơn đã `APPROVED`/`REJECTED` thì không sửa lại được.
3. Sau khi có quyết định, hệ thống gửi thông báo (email - ở bản mini này được **mock**, xem `docs/architecture.md`).
4. HR có thể xem toàn bộ đơn bất kỳ lúc nào, lọc theo trạng thái.

## Ngoài phạm vi (out of scope) của bản mini

- Không có màn đăng ký tài khoản mới - user được seed sẵn trong `data.sql` (một công ty nhỏ, danh sách nhân sự cố định).
- Không tính số ngày phép còn lại/quy tắc nghỉ phép phức tạp (nghỉ có lương/không lương, số ngày tối đa...).
- Không có chuỗi duyệt nhiều cấp (multi-level approval) - chỉ 1 quản lý trực tiếp duyệt là xong.
