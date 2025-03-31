import { createRouter, createWebHistory } from "vue-router";
import StartMain from "../components/StartMain.vue";
import StockMarket from "../components/StockMarket.vue";
import StockGraph from "../components/StockGraph.vue";

const routes = [
  { path: "/", component: StartMain },
  { path: "/stock", component: StockMarket },
  { path: "/stockchart", component: StockGraph },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
