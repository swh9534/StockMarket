import vue from "@vitejs/plugin-vue";
import { fileURLToPath, URL } from "node:url";
import { defineConfig } from "vite";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  build: {
    outDir: "../backend/src/main/resources/static",
  },
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
      },
    },
    historyApiFallback: true, // 여기에 추가
  },
});
