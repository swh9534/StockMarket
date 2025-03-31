<script setup>
import { ref, reactive, onMounted } from "vue";
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

const props = defineProps({
  stockName: {
    type: String,
    required: true,
  },
});

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
  scales: {
    x: {
      title: {
        display: true,
        text: "날짜",
      },
    },
    y: {
      title: {
        display: true,
        text: "가격",
      },
    },
  },
};

const getStockGraphData = async () => {
  if (!props.stockName) {
    console.warn("주식 이름이 제공되지 않았습니다.");
    chartData.labels = [];
    chartData.datasets[0].data = [];
    return;
  }

  const url = `/api/stocks/${props.stockName}/history`;
  try {
    const response = await apiCall.get(url, null, null);
    console.log("주식 그래프 데이터 응답:", response);

    if (Array.isArray(response) && response.length > 0) {
      // 날짜를 가독성 있게 포맷팅 (예: "03-31 10:34")
      chartData.labels = response.map((item) => {
        const date = new Date(item.date);
        return `${
          date.getMonth() + 1
        }-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`;
      });
      chartData.datasets[0].data = response.map((item) => item.price);
    } else {
      console.warn("히스토리 데이터가 없습니다.");
      chartData.labels = [];
      chartData.datasets[0].data = [];
    }
  } catch (error) {
    console.error("그래프 데이터 요청 실패:", error.message);
    chartData.labels = [];
    chartData.datasets[0].data = [];
  }
};

onMounted(() => {
  getStockGraphData();
});
</script>

<template>
  <div style="width: 100%; height: 400px">
    <Line
      v-if="chartData.labels.length > 0"
      :data="chartData"
      :options="chartOptions"
    />
    <p v-else class="text-center mt-5">그래프 데이터를 불러올 수 없습니다.</p>
  </div>
</template>
