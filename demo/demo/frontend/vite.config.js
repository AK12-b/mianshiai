import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

/**
 * 访问地址说明：
 * - 走 Spring Boot 静态资源：先启动后端（默认端口 8080），浏览器打开 http://localhost:8080/
 *   不要只输入 http://localhost（默认 80 端口无服务会 ERR_CONNECTION_REFUSED）。
 * - 改 Vue 后要让 8080 刷新即看到最新：在 frontend 目录执行 npm run watch:spring，保存文件会自动构建到 ../src/main/resources/static
 * - 纯前端热更新开发：npm run dev → http://localhost:5173（需后端 8080 已启动，/api 会走代理）
 */
export default defineConfig({
  plugins: [vue()],
  server: {
    // 监听所有网卡，避免部分环境下 localhost 解析到 IPv6 而 dev 只绑 IPv4 导致拒绝连接
    host: true,
    port: 5173,
    strictPort: true,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  },
  build: {
    // 构建后可直接输出到 Spring Boot 静态目录
    outDir: "../src/main/resources/static",
    emptyOutDir: true
  },
  base: "./"
});

