<script setup>
import { usePlayer } from "@/scripts/store-player";
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import apiCall from "@/scripts/api-call";
import { notifyError } from "@/scripts/store-popups";


const router = useRouter();

const stockId = ref("");
const stockQuantity = ref("");

const table = reactive({
  headers: [
    { label: "주식ID", value: "id" },
    { label: "주식명", value: "stockName" },
    { label: "주식가격", value: "stockPrice" },
    { label: "보유수량", value: "stockQuantity" },
  ],
  items: [],
});

const player = usePlayer();

const getPlayerInfo = async () => {
  console.log("playerId:", player.playerId);

  const playerId = player.currentUser?.playerId || player.playerId;

  if (!playerId) {
    console.warn("playerId가 없습니다. 정보를 가져올 수 없습니다.");
    return;
  }

  const url = `/api/players/${playerId}`;
  const response = await apiCall.get(url, null, null);
  console.log("getPlayerInfo 응답:", response);

  if (response.result === apiCall.Response.SUCCESS && response.body) {
    table.items = response.body.playerStockList || [];
    player.playerMoney = response.body.playerMoney || 0;
  } else {
    notifyError("플레이어 정보를 불러오는 데 실패했습니다.");
    console.warn("플레이어 응답 데이터 이상:", response);
  }
};

const buyPlayerStock = async () => {
  const url = "/api/players/buy";
  const data = {
    playerId: player.playerId,
    stockId: stockId.value,
    stockQuantity: stockQuantity.value,
  };

  console.log("구매 요청 데이터:", data);

  const response = await apiCall.post(url, null, data);
  if (response.result === apiCall.Response.SUCCESS) {
    getPlayerInfo();
    stockId.value = "";
    stockQuantity.value = "";
  }
};

const sellPlayerStock = async () => {
  const url = "/api/players/sell";
  const data = {
    playerId: player.playerId,
    stockId: parseInt(stockId.value, 10),
    stockQuantity: parseInt(stockQuantity.value, 10),
  };

  console.log("판매 요청 데이터:", data);

  const response = await apiCall.post(url, null, data);
  if (response.result === apiCall.Response.SUCCESS) {
    getPlayerInfo();
    stockId.value = "";
    stockQuantity.value = "";
  }
};

onMounted(() => {
  console.log("playerId:", player.playerId);
  if (player.playerId) {
    getPlayerInfo();
  } else {
    console.warn("playerId 없음");
  }
});

</script>

<template>
  <div class="row mt-2">
    <span class="fs-4"
      ><i class="bi bi-person m-2"></i>{{ player.playerId }} 플레이어</span
    >
  </div>
  <div class="row border-bottom">
    <div class="col d-flex justify-content-end">
      <button class="btn btn-sm btn-primary m-1" @click="getPlayerInfo">
        <i class="bi bi-arrow-counterclockwise m-2"></i>갱신
      </button>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <InlineInput
        class="m-2"
        label="플레이어ID"
        v-model="player.playerId"
        :disabled="true"
      />
      <InlineInput
        class="m-2"
        label="보유금액"
        v-model="player.playerMoney"
        :disabled="true"
      />
    </div>
  </div>
  <div class="row g-2 align-items-center m-2 mt-0">
    <div class="col-2 d-flex justify-content-end">
      <label class="col-form-label form-control-sm p-1">보유주식목록</label>
    </div>
    <div class="col">
      <ItemsTable
        :headers="table.headers"
        :items="table.items"
        :nosetting="true"
      />
    </div>
  </div>
  <div class="row g-2 align-items-center m-2 mt-0">
    <div class="col-2 d-flex justify-content-end">
      <label class="col-form-label form-control-sm p-1">주식선택</label>
    </div>
    <div class="col">
      <InlineInput v-model="stockId" placeholder="주식ID" />
    </div>
    <div class="col">
      <InlineInput v-model="stockQuantity" placeholder="주식수량" />
    </div>
    <div class="col d-flex justify-content-start">
      <button
        class="btn btn-sm btn-outline-primary m-1"
        @click="buyPlayerStock"
      >
        주식 구매
      </button>
      <button
        class="btn btn-sm btn-outline-primary m-1"
        @click="sellPlayerStock"
      >
        주식 판매
      </button>
    </div>
  </div>
</template>
