<script setup>
import { ref, reactive, onMounted, watch, nextTick, computed } from "vue";
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

// 원본 데이터 저장
const originalData = ref({
  labels: [],
  prices: [],
});

// 현재 선택된 간격 (1, 5, 12 또는 60)
const selectedInterval = ref(1);

// 표시할 데이터
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

// 스크롤 컨테이너 참조
const scrollContainerRef = ref(null);

// 차트 옵션 설정
const chartOptions = reactive({
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    x: {
      title: {
        display: false,
        text: "날짜",
      },
      ticks: {
        autoSkip: true,
        maxRotation: 90,
        minRotation: 45,
        maintainAspectRatio: false,
      },
    },
    y: {
      title: {
        display: true,
        text: "가격",
      },
    },
  },
});

// 스크롤을 가장 오른쪽으로 이동시키는 함수
const scrollToEnd = () => {
  if (scrollContainerRef.value) {
    nextTick(() => {
      scrollContainerRef.value.scrollLeft =
        scrollContainerRef.value.scrollWidth;
    });
  }
};

// 간격에 따라 데이터 필터링
const filterDataByInterval = (interval) => {
  if (originalData.value.labels.length === 0) return;

  const filteredLabels = [];
  const filteredPrices = [];

  for (let i = 0; i < originalData.value.labels.length; i += interval) {
    filteredLabels.push(originalData.value.labels[i]);
    filteredPrices.push(originalData.value.prices[i]);
  }

  chartData.labels = filteredLabels;
  chartData.datasets[0].data = filteredPrices;

  // 데이터 필터링 후 차트 너비 업데이트
  updateChartWidth();

  // 필터링 후 스크롤을 끝으로 이동
  nextTick(() => {
    scrollToEnd();
  });
};

// 간격 변경 핸들러
const changeInterval = (interval) => {
  selectedInterval.value = interval;
  filterDataByInterval(interval);
};

const getStockGraphData = async () => {
  if (!props.stockName) {
    console.warn("주식 이름이 제공되지 않았습니다.");
    originalData.value.labels = [];
    originalData.value.prices = [];
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
      originalData.value.labels = response.map((item) => {
        const date = new Date(item.date);
        return `${(date.getMonth() + 1).toString().padStart(2, "0")}-${date
          .getDate()
          .toString()
          .padStart(2, "0")} ${date
          .getHours()
          .toString()
          .padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")}`;
      });
      originalData.value.prices = response.map((item) => item.price);

      // 현재 선택된 간격으로 데이터 필터링
      filterDataByInterval(selectedInterval.value);
    } else {
      console.warn("히스토리 데이터가 없습니다.");
      originalData.value.labels = [];
      originalData.value.prices = [];
      chartData.labels = [];
      chartData.datasets[0].data = [];
    }
  } catch (error) {
    console.error("그래프 데이터 요청 실패:", error.message);
    originalData.value.labels = [];
    originalData.value.prices = [];
    chartData.labels = [];
    chartData.datasets[0].data = [];
  }
};

// 차트 컨테이너의 너비를 데이터 포인트 수에 맞게 동적으로 조정
const chartContainerStyle = ref({
  width: "1000px",
  height: "400px",
});

const updateChartWidth = () => {
  const pointCount = chartData.labels.length;
  // 각 데이터 포인트당 최소 20px의 너비를 확보
  const minWidth = Math.max(pointCount * 20, 800);
  chartContainerStyle.value = {
    width: `${minWidth}px`,
    height: "400px",
  };
};

// 간격 버튼 스타일 계산
const getButtonClass = (interval) => {
  return {
    btn: true,
    "btn-sm": true,
    "btn-primary": selectedInterval.value === interval,
    "btn-outline-primary": selectedInterval.value !== interval,
  };
};

// stockName이 변경될 때마다 데이터를 다시 가져오도록 watch 추가
watch(
  () => props.stockName,
  () => {
    getStockGraphData();
  },
  { immediate: false }
);

onMounted(() => {
  getStockGraphData();
});
</script>

<template>
  <div class="chart-outer-container">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h3 v-if="props.stockName" class="chart-title mb-0">
        {{ props.stockName }} 가격 추이
      </h3>

      <div class="interval-controls">
        <span class="me-2">간격:</span>
        <button
          :class="getButtonClass(1)"
          @click="changeInterval(1)"
          title="모든 데이터 포인트 표시"
        >
          1개
        </button>
        <button
          :class="getButtonClass(5)"
          @click="changeInterval(5)"
          title="5개 단위로 표시"
          class="ms-1"
        >
          5개
        </button>
        <button
          :class="getButtonClass(12)"
          @click="changeInterval(12)"
          title="12개 단위로 표시"
          class="ms-1"
        >
          12개
        </button>
        <button
          :class="getButtonClass(60)"
          @click="changeInterval(60)"
          title="60개 단위로 표시"
          class="ms-1"
        >
          60개
        </button>
      </div>
    </div>

    <div class="chart-scroll-container" ref="scrollContainerRef">
      <div :style="chartContainerStyle" class="chart-inner-container">
        <Line
          v-if="chartData.labels.length > 0"
          :data="chartData"
          :options="chartOptions"
        />
        <p v-else class="text-center mt-5">
          그래프 데이터를 불러올 수 없습니다.
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chart-outer-container {
  width: 100%;
  margin: 0 auto;
}

.chart-title {
  margin-bottom: 16px;
}

.chart-scroll-container {
  width: 100%;
  overflow-x: auto;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}

.chart-inner-container {
  /* 최소 너비 설정, 스크립트에서 동적으로 업데이트됨 */
  min-width: 800px;
}

.interval-controls {
  display: flex;
  align-items: center;
}
</style>
