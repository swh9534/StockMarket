import { createRouter, createWebHistory } from "vue-router";
import StartMain from "../components/StartMain.vue";
import StockMarket from "../components/StockMarket.vue";
import AdminMain from "../components//AdminMain.vue";
import path from "path";

const routes = [
  { path: "/", component: StartMain },
  { path: "/stock", component: StockMarket },
  { path: "/admin", component: AdminMain },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
