<script setup>
import { ref, onMounted } from "vue";
import PlayerStocks from "./components/PlayerStocks.vue";
import StockList from "./components/StockList.vue";

const playerId = ref("");

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
          <StockList />
        </div>
        <div class="col-8 border m-1">
          <PlayerStocks />
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
