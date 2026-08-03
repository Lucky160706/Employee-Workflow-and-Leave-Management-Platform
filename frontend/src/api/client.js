const TOKEN_KEY = 'leave_app_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  if (token) localStorage.setItem(TOKEN_KEY, token)
  else localStorage.removeItem(TOKEN_KEY)
}

/**
 * 1 fetch wrapper duy nhat: tu gan header Authorization neu da dang nhap, va
 * nem loi voi dung message tu backend (GlobalExceptionHandler tra ve {error: "..."})
 * thay vi de caller tu parse response o moi noi goi API.
 */
export async function apiFetch(path, options = {}) {
  const token = getToken()
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  }

  const response = await fetch(path, { ...options, headers })

  // 401 = token het han hoac khong hop le (backend phan biet 401 vs 403 - xem
  // SecurityConfig). Xoa token chet va quay ve man dang nhap ngay tai day, thay vi
  // de tung man hinh hien "loi" roi nguoi dung ket lai voi 1 token khong dung duoc.
  // 403 KHONG xu ly nhu vay: da dang nhap dung nguoi, chi la khong du quyen.
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
