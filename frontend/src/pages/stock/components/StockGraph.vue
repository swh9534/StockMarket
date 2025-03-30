<script setup>
import { onMounted, reactive, ref } from "vue";
import { useRoute } from "vue-router";
import apiCall from "@/scripts/api-call";
import { Line } from "vue-chartjs";
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  CategoryScale,
  LinearScale,
} from "chart.js";

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  CategoryScale,
  LinearScale
);

const route = useRoute();
const stockName = ref("");
const chartData = reactive({
  labels: [],
  datasets: [
    {
      label: "주식 가격",
      data: [],
      borderColor: "#36A2EB",
      fill: false,
    },
  ],
});
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
};

const getStockGraphData = async () => {
  stockName.value = route.query.stockName || "";
  if (!stockName.value) {
    console.warn("주식 이름이 제공되지 않았습니다.");
    return;
  }

  const url = `/api/stocks/${stockName.value}/history`; // 가정된 API 엔드포인트
  try {
    const response = await apiCall.get(url, null, null);
    console.log("주식 그래프 데이터 응답:", response);

    if (
      response.result === apiCall.Response.SUCCESS &&
      Array.isArray(response.body)
    ) {
      chartData.labels = response.body.map((item) => item.date); // 날짜 배열
      chartData.datasets[0].data = response.body.map((item) => item.price); // 가격 배열
    } else {
      console.warn("그래프 데이터 비정상 응답:", response.message);
    }
  } catch (error) {
    console.error("그래프 데이터 요청 실패:", error.message);
  }
};

onMounted(() => {
  getStockGraphData();
});
</script>

<template>
  <div class="container-fluid">
    <div class="row mt-2">
      <span class="fs-4"
        ><i class="bi bi-graph-up m-2"></i>{{ stockName }} 그래프</span
      >
    </div>
    <div style="width: 100%; height: 400px">
      <Line :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>
