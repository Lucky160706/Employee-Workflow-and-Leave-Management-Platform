import { useCallback, useEffect, useState } from 'react'
import { apiFetch } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { ErrorBanner } from '../components/ErrorBanner'
import { LeaveRequestForm } from '../components/LeaveRequestForm'
import { LeaveRequestTable } from '../components/LeaveRequestTable'

export function EmployeeDashboard() {
  const { user, logout } = useAuth()
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const loadMine = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      setRows(await apiFetch('/api/v1/leave-requests/mine'))
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadMine()
  }, [loadMine])

  return (
    <div className="dashboard">
      <header>
        <h2>Xin chào, {user.fullName}</h2>
        <button onClick={logout}>Đăng xuất</button>
      </header>

      <LeaveRequestForm onCreated={loadMine} />

      <h3>Lịch sử đơn của tôi</h3>
      <ErrorBanner message={error} />
      {loading ? <p>Đang tải...</p> : <LeaveRequestTable rows={rows} />}
    </div>
  )
}
