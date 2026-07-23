import { Navigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

/** allowedRoles rong = chi can dang nhap, khong quan tam role nao. */
export function ProtectedRoute({ children, allowedRoles = [] }) {
  const { user, hydrating } = useAuth()

  if (hydrating) return <p>Đang tải...</p>
  if (!user) return <Navigate to="/login" replace />
  if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
    return <Navigate to="/login" replace />
  }
  return children
}
