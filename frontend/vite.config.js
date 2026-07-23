import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Proxy /api tới backend Stage 2 khi chạy `npm run dev` - tránh lỗi CORS mà
// không phải cấu hình CORS ở backend chỉ để phục vụ local dev.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
