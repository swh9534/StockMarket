<script setup>
import { ref, onMounted } from "vue";
import PlayerStocks from "./components/PlayerStocks.vue";
import StockList from "./components/StockList.vue";
import StockGraph from "./components/StockGraph.vue"; // StockGraph 컴포넌트 추가

const playerId = ref("");
const selectedStock = ref(null); // 선택된 주식

const handleSelectStock = (stock) => {
  selectedStock.value = stock; // 선택된 주식 업데이트
};

onMounted(() => {
  const currentUser = JSON.parse(localStorage.getItem("currentUser") || "{}");
  if (currentUser?.playerId) {
    playerId.value = currentUser.playerId;
  }
});
</script>

<template>
  <div class="container-fluid">
    <div class="row bg-body-tertiary">
      <div class="col">
        <span class="ps-2 fs-2">SKALA STOCK Market</span>
      </div>
    </div>

    <template v-if="playerId">
      <div class="row">
        <div class="col-3 border m-1">
          <!-- StockList 컴포넌트로부터 주식 선택 시 이벤트 전달받음 -->
          <StockList @select-stock="handleSelectStock" />
        </div>
        <div class="col-8 border m-1">
          <PlayerStocks />
        </div>
      </div>

      <!-- 선택된 주식의 그래프 표시 -->
      <div class="row mt-4">
        <div class="col-12">
          <StockGraph
            v-if="selectedStock"
            :stockName="selectedStock?.stockName"
          />
        </div>
      </div>
    </template>

    <template v-else>
      <div class="row mt-4">
        <div class="col text-center text-danger fs-5">
          플레이어 정보가 없습니다. 로그인 후 이용해 주세요.
        </div>
      </div>
    </template>
  </div>
</template>
