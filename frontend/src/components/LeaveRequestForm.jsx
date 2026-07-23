import { useState } from 'react'
import { apiFetch } from '../api/client'
import { ErrorBanner } from './ErrorBanner'

export function LeaveRequestForm({ onCreated }) {
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [reason, setReason] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    if (!startDate || !endDate || !reason.trim()) {
      setError('Vui lòng điền đủ ngày bắt đầu, ngày kết thúc và lý do.')
      return
    }
    if (endDate < startDate) {
      setError('Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.')
      return
    }

    setSubmitting(true)
    try {
      await apiFetch('/api/v1/leave-requests', {
        method: 'POST',
        body: JSON.stringify({ startDate, endDate, reason }),
      })
      setStartDate('')
      setEndDate('')
      setReason('')
      onCreated()
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form className="leave-form" onSubmit={handleSubmit}>
      <h3>Gửi đơn xin nghỉ</h3>
      <ErrorBanner message={error} />
      <label>
        Ngày bắt đầu
        <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
      </label>
      <label>
        Ngày kết thúc
        <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
      </label>
      <label>
        Lý do
        <textarea value={reason} onChange={(e) => setReason(e.target.value)} rows={3} />
      </label>
      <button type="submit" disabled={submitting}>
        {submitting ? 'Đang gửi...' : 'Gửi đơn'}
      </button>
    </form>
  )
}
