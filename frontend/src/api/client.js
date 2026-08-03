const TOKEN_KEY = 'leave_app_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  if (token) localStorage.setItem(TOKEN_KEY, token)
  else localStorage.removeItem(TOKEN_KEY)
}

/**
 * Moi loi goi API deu di qua day. Tu gan header Authorization neu da dang nhap, va
 * nem loi kem dung message backend tra ve, de tung man hinh khoi phai tu parse response.
 */
export async function apiFetch(path, options = {}) {
  const token = getToken()
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  }

  const response = await fetch(path, { ...options, headers })

  // 401 la token het han hoac khong hop le. Xoa luon roi da ve man dang nhap ngay tai
  // day, chu de tung man hinh tu hien "loi" thi nguoi dung ket lai voi mot token chet
  // ma khong hieu tai sao. 403 thi khong lam vay: dung nguoi, chi la khong du quyen.
  if (response.status === 401 && token) {
    setToken(null)
    if (window.location.pathname !== '/login') window.location.assign('/login')
  }

  if (!response.ok) {
    let message = `Request failed (${response.status})`
    try {
      const body = await response.json()
      message = body.error || message
    } catch {
      // response khong phai JSON (vi du 401 tu container) - giu message mac dinh
    }
    throw new Error(message)
  }

  if (response.status === 204) return null
  return response.json()
}
