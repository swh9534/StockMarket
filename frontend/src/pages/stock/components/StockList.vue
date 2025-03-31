<script setup>
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import apiCall from "@/scripts/api-call";
import { notifyError } from "@/scripts/store-popups";

// 이벤트 정의
const emit = defineEmits();

const stockName = ref("");
const stockPrice = ref("");
const table = reactive({
  headers: [
    { label: "주식명", value: "stockName" },
    { label: "주식가격", value: "stockPrice" },
  ],
  items: [],
});

const isLoading = ref(false); // 로딩 상태를 추적

// 선택된 주식
const selectedStock = ref(null);

const getStockList = async () => {
  if (isLoading.value) {
    console.log("이미 요청 중입니다. 중복 요청 방지됨.");
    return;
  }

  isLoading.value = true;
  const url = "/api/stocks";

  try {
    const response = await apiCall.get(url, null, null);
    console.log("받은 주식 데이터:", response);

    if (Array.isArray(response)) {
      table.items = [...response];
      console.log("업데이트된 테이블:", table);
    } else {
      notifyError("받은 데이터가 배열 형식이 아닙니다.");
      console.warn("비정상적인 응답 데이터:", response);
    }
  } catch (error) {
    const errorMessage = error.message || "알 수 없는 오류";
    notifyError(`주식 목록을 불러오는 데 실패했습니다: ${errorMessage}`);
    console.error("주식 목록 요청 에러:", error);
  } finally {
    isLoading.value = false;
  }
};

const showStockGraph = (stock) => {
  selectedStock.value = stock; // 선택된 주식 업데이트
  emit("select-stock", stock); // 부모로 이벤트 전달
};

onMounted(() => {
  console.log("onMounted 실행됨 - 주식 목록 요청 시작");
  getStockList();
});
</script>

<template>
  <div class="row mt-2">
    <span class="fs-4"><i class="bi bi-graph-up m-2"></i>주식목록</span>
  </div>
  <div class="row border-bottom">
    <div class="col d-flex justify-content-end">
      <button class="btn btn-sm btn-primary m-1" @click="getStockList">
        <i class="bi bi-arrow-counterclockwise m-2"></i>갱신
      </button>
    </div>
  </div>
  <div
    class="row g-2 align-items-center m-2 mt-0"
    style="height: 400px; overflow-y: auto"
  >
    <div class="col">
      <ItemsTable
        :headers="table.headers"
        :items="table.items"
        :nosetting="true"
        @row-click="showStockGraph"
      />
    </div>
  </div>

  <!-- 주식 그래프 컴포넌트 -->
  <div v-if="selectedStock" class="mt-4">
    <!-- key 값을 selectedStock에 stockName을 사용하여 설정 -->
    <StockGraph
      :key="selectedStock.stockName"
      :stockName="selectedStock.stockName"
    />
  </div>
</template>

<style scoped>
.row.g-2.align-items-center.m-2.mt-0 {
  max-height: 400px;
  overflow-y: auto;
}
</style>
