const STATUS_LABEL = {
  PENDING: 'Chờ duyệt',
  APPROVED: 'Đã duyệt',
  REJECTED: 'Từ chối',
}

/** onDecide truyen vao thi moi hien nut Duyet/Tu choi (chi ManagerDashboard dung). */
export function LeaveRequestTable({ rows, onDecide }) {
  if (rows.length === 0) return <p>Chưa có đơn nào.</p>

  return (
    <table className="leave-table">
      <thead>
        <tr>
          <th>Nhân viên</th>
          <th>Từ ngày</th>
          <th>Đến ngày</th>
          <th>Lý do</th>
          <th>Trạng thái</th>
          {onDecide && <th>Hành động</th>}
        </tr>
      </thead>
      <tbody>
        {rows.map((r) => (
          <tr key={r.id}>
            <td>{r.requesterFullName}</td>
            <td>{r.startDate}</td>
            <td>{r.endDate}</td>
            <td>{r.reason}</td>
            <td>
              <span className={`status status-${r.status.toLowerCase()}`}>
                {STATUS_LABEL[r.status] ?? r.status}
              </span>
            </td>
            {onDecide && (
              <td>
                {r.status === 'PENDING' ? (
                  <>
                    <button onClick={() => onDecide(r.id, 'APPROVED')}>Duyệt</button>
                    <button onClick={() => onDecide(r.id, 'REJECTED')}>Từ chối</button>
                  </>
                ) : (
                  '—'
                )}
              </td>
            )}
          </tr>
        ))}
      </tbody>
    </table>
  )
}
