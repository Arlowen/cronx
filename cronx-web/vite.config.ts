import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/cronx': {
        target: 'http://127.0.0.1:8555',
        changeOrigin: true,
      }
    }
  }
})
