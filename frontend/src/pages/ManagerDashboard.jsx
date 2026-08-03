import { useCallback, useEffect, useState } from 'react'
import { apiFetch } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { ErrorBanner } from '../components/ErrorBanner'
import { LeaveRequestTable } from '../components/LeaveRequestTable'

/**
 * MANAGER va HR dung chung man nay. Hai role chi khac nhau o cho lay du lieu (doi minh
 * hay toan cong ty) va co nut duyet hay khong, khong dang tach thanh hai file cho mot
 * cai bang giong het nhau.
 */
export function ManagerDashboard() {
  const { user, logout } = useAuth()
  const isHr = user.role === 'HR'
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const path = isHr ? '/api/v1/leave-requests' : '/api/v1/leave-requests/team'
      setRows(await apiFetch(path))
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [isHr])

  useEffect(() => {
    load()
  }, [load])

  async function handleDecide(id, decision) {
    setError('')
    try {
      await apiFetch(`/api/v1/leave-requests/${id}/decision`, {
        method: 'PATCH',
        body: JSON.stringify({ decision }),
      })
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="dashboard">
      <header>
        <h2>Xin chào, {user.fullName} ({isHr ? 'HR' : 'Quản lý'})</h2>
        <button onClick={logout}>Đăng xuất</button>
      </header>

      <h3>{isHr ? 'Toàn bộ đơn nghỉ phép' : 'Đơn của đội bạn'}</h3>
      <ErrorBanner message={error} />
      {loading ? <p>Đang tải...</p> : <LeaveRequestTable rows={rows} onDecide={isHr ? undefined : handleDecide} />}
    </div>
  )
}
