<script setup>
import { onMounted, reactive, ref, watch } from "vue";
import apiCall from "@/scripts/api-call";
import { notifyError } from "@/scripts/store-popups";

const stockName = ref("");
const stockPrice = ref("");
const table = reactive({
  headers: [
    { label: "주식ID", value: "id" },
    { label: "주식명", value: "stockName" },
    { label: "주식가격", value: "stockPrice" },
  ],
  items: [],
});

const page = reactive({
  total: 0,
  current: 1,
  count: 10,
});

const getStockList = async () => {
  table.items = [];
  const url = "/api/stocks/list";

  const queryParams = {
    count: page.count,
    offset: page.current - 1,
  };

  const { body: pagedList } = await apiCall.get(url, null, queryParams);

  if (pagedList && typeof pagedList === "object") {
    page.total = pagedList.total ?? 0;

    if (pagedList.offset !== undefined) {
      page.current = pagedList.offset + 1;
    }

    if (Array.isArray(pagedList.list)) {
      table.items = pagedList.list;
    }
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
    getStockList();
  }
};

watch(
  () => page.current,
  () => {
    console.log("📌 Page changed:", page.current);
    getStockList();
  }
);

onMounted(() => {
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
  <div class="row g-2 align-items-center m-2 mt-0">
    <div class="col">
      <ItemsTable
        :headers="table.headers"
        :items="table.items"
        :nosetting="true"
      />
      <PageNavigator
        :totalCount="page.total"
        v-model:current="page.current"
        v-model:count="page.count"
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
