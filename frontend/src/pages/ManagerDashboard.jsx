import { useCallback, useEffect, useState } from 'react'
import { apiFetch } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { ErrorBanner } from '../components/ErrorBanner'
import { LeaveRequestTable } from '../components/LeaveRequestTable'

/**
 * Dung chung cho MANAGER va HR: 2 role khac nhau ve dieu duoc fetch (team vs toan bo)
 * va co duoc duyet don khong - khong tach thanh 2 dashboard rieng cho 1 bang giong het nhau.
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
