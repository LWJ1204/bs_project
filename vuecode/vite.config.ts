import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'; // 使用 Node.js 的 path 模块
// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve:{
    extensions: ['.js','.vue','.json','.ts'],
    alias: {
        '@': resolve(__dirname, './src'),
    }
  },
  server:{
    proxy:{
      '/api':{
        target:"http://localhost:8080/",
        changeOrigin:true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
})
