<script setup>
import { onMounted, reactive, ref } from "vue";
import apiCall from "@/scripts/api-call";
import { notifyError } from "@/scripts/store-popups";



const stockName = ref("");
const stockPrice = ref("");
const table = reactive({
  headers: [
    { label: "주식ID", value: "stockId" },
    { label: "주식명", value: "stockName" },
    { label: "주식가격", value: "stockPrice" },
  ],
  items: [],
});

const isLoading = ref(false); // 로딩 상태를 추적
let currentOffset = 0;

const getStockList = async () => {
  console.log("getStockList 시작");

  if (isLoading.value) return; // 중복 요청 방지

  isLoading.value = true;
  const url = "/api/stocks";

  try {
    const response = await apiCall.get(url, null, null);

    console.log("받은 주식 데이터:", response);

    if (Array.isArray(response)) {
      table.items = [...response];
      console.log("table", table);
    } else {
      notifyError("받은 데이터가 배열이 아닙니다.");
      console.warn("주식 데이터가 비정상입니다:", response);
    }
  } catch (error) {
    notifyError("주식 목록을 불러오는 데 실패했습니다.");
    console.error("주식 목록 요청 에러:", error);
  } finally {
    isLoading.value = false;
  }
};

const addStock = async () => {
  if (!stockName.value || !stockPrice.value) {
    notifyError("주식명과 주식가격을 모두 입력하세요.");
    return;
  }

  const url = "/api/stocks";
  const stockData = {
    stockName: stockName.value,
    stockPrice: parseInt(stockPrice.value, 10),
  };

  const response = await apiCall.post(url, null, stockData);
  if (response.result === apiCall.Response.SUCCESS) {
    stockName.value = "";
    stockPrice.value = "";
    getStockList(); // 주식 목록 갱신
  }
};

// 스크롤 감지
const handleScroll = (event) => {
  const bottom =
    event.target.scrollHeight ===
    event.target.scrollTop + event.target.clientHeight;
  if (bottom) {
    getStockList(); // 스크롤 끝에 도달하면 목록을 추가로 불러옴
  }
};

onMounted(() => {
  console.log("onMounted 실행됨");
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
    @scroll="handleScroll"
    style="height: 400px; overflow-y: auto"
  >
    <div class="col">
      <ItemsTable
        :headers="table.headers"
        :items="table.items"
        :nosetting="true"
      />
    </div>
  </div>
  <div class="row g-2 m-2 border-top">
    <div class="col-2 d-flex justify-content-end">
      <label class="col-form-label form-control-sm p-1">주식정보</label>
    </div>
    <div class="col">
      <InlineInput v-model="stockName" placeholder="주식명" />
    </div>
    <div class="col">
      <InlineInput v-model="stockPrice" placeholder="주식가격" />
    </div>
    <div class="col d-flex justify-content-start">
      <button class="btn btn-sm btn-outline-primary me-2" @click="addStock">
        주식 추가
      </button>
    </div>
  </div>
</template>

<style scoped>
.row.g-2.align-items-center.m-2.mt-0 {
  max-height: 400px;
  overflow-y: auto;
}
</style>
