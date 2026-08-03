import { createContext, useContext, useEffect, useState } from 'react'
import { apiFetch, getToken, setToken } from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  // Dang kiem token cu con dung khong truoc khi cho vao app. Khong co buoc nay thi
  // man dang nhap loe len mot cai roi moi nhay vao dashboard, nhin rat gion.
  const [hydrating, setHydrating] = useState(true)

  useEffect(() => {
    async function hydrate() {
      if (getToken()) {
        try {
          const me = await apiFetch('/api/v1/auth/me')
          setUser(me)
        } catch {
          setToken(null)
        }
      }
      setHydrating(false)
    }
    hydrate()
  }, [])

  async function login(email, password) {
    const result = await apiFetch('/api/v1/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    })
    setToken(result.token)
    setUser({ email, role: result.role, fullName: result.fullName })
    return result
  }

  function logout() {
    setToken(null)
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, hydrating, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
