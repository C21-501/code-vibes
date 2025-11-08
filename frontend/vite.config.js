import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    strictPort: true,
    proxy: {
      // Бэкенд API
      '^/(user|team|system|rfc|attachment|api)': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // Keycloak - используем порт 8081 для разработки
      '^/(realms|protocol|admin|resources)': {
        target: 'http://localhost:8081',  // Порт 8081 на хосте
        changeOrigin: true
      }
    }
  },
  preview: {
    host: '0.0.0.0',
    port: 80
  }
})