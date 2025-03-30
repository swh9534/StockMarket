import { createRouter, createWebHistory } from "vue-router";
import StartMain from "../components/StartMain.vue";
import StockMarket from "../components/StockMarket.vue";

const routes = [
  { path: "/", component: StartMain },
  { path: "/stock", component: StockMarket },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
