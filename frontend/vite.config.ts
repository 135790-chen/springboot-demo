import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/auth': 'http://localhost:8080',
      '/api': 'http://localhost:8080',
      '/students': 'http://localhost:8080',
      '/kafka': 'http://localhost:8080',
      '/organization': 'http://localhost:8080',
      '/teaching': 'http://localhost:8080',
      '/statistics': 'http://localhost:8080'
    }
  },
  build: {
    outDir: '../gateway/src/main/resources/static',
    emptyOutDir: true
  }
})
