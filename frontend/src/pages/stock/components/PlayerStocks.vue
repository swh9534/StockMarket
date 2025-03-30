<script setup>
import { usePlayer } from "@/scripts/store-player";
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import apiCall from "@/scripts/api-call";
import { notifyError } from "@/scripts/store-popups";
import { notifySuccess } from "@/scripts/store-popups";

const router = useRouter();

const stockName = ref(""); // 주식 이름
const stockQuantity = ref(""); // 주식 수량
const player = usePlayer();

// 테이블 데이터
const table = reactive({
  headers: [
    { label: "주식명", value: "stockName" },
    { label: "가격", value: "stockPrice" },
    { label: "수량", value: "stockQuantity" },
    { label: "투자금액", value: "investedAmount" },
    { label: "현재 가치", value: "totalValue" },
    { label: "수익률", value: "profitRate" },
  ],
  items: [],
});

// 플레이어 정보와 포트폴리오 정보 가져오기
const getPlayerInfo = async () => {
  const playerId = player.currentUser?.playerId || player.playerId;
  if (!playerId) {
    console.warn("playerId가 없습니다. 정보를 가져올 수 없습니다.");
    return;
  }

  // 기본 플레이어 정보 가져오기
  const playerUrl = `/api/players/${playerId}`;
  const playerResponse = await apiCall.justGet(playerUrl, null, null);
  if (playerResponse.playerId) {
    player.playerMoney = playerResponse.cash || 0;
  } else {
    notifyError("플레이어 정보를 불러오지 못했습니다.");
  }

  // 포트폴리오 정보 가져오기
  const portfolioUrl = `/api/players/${playerId}/portfolio`;
  try {
    const portfolioResponse = await apiCall.justGet(portfolioUrl, null, null);
    console.log("포트폴리오 응답:", portfolioResponse);

    if (Array.isArray(portfolioResponse)) {
      // 데이터 변환하여 사용
      table.items = portfolioResponse.map((item) => ({
        id: item.id,
        stockName: item.stockName,
        stockPrice: item.stock.stockPrice,
        stockQuantity: item.quantity,
        investedAmount: item.investedAmount,
        totalValue: item.totalValue,
        profitRate: item.profitRate.toFixed(2) + "%",
      }));
    }

    console.log("설정된 주식 목록:", table.items);
  } catch (error) {
    console.error("포트폴리오 정보 가져오기 실패:", error);
    notifyError("주식 목록을 불러오지 못했습니다.");
  }
};

// 주식 거래 (매수/매도)
const tradeStock = async (action) => {
  if (!player.playerId || !stockName.value || !stockQuantity.value) {
    notifyError("필수 입력값을 확인해주세요.");
    return;
  }

  const url = `/api/players/${player.playerId}/${action}`;
  const data = {
    stockName: stockName.value,
    quantity: parseInt(stockQuantity.value, 10),
  };

  console.log(`${action} 요청 데이터:`, data);

  try {
    const response = await apiCall.post(url, null, data);
    console.log(`${action} 응답 데이터:`, response);

    // 응답이 성공적인 경우
    if (response && response.message && response.message.includes("완료")) {
      notifySuccess(`${action === "buy" ? "주식 구매" : "주식 판매"} 성공!`);
      getPlayerInfo();
      stockName.value = "";
      stockQuantity.value = "";
    } else {
      notifyError("거래 실패: 서버 응답 오류");
      console.error("거래 실패:", response);
    }
  } catch (error) {
    notifyError("거래 실패: " + error.message);
  }
};

onMounted(() => {
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
      <select v-model="stockName" class="form-control form-control-sm">
        <option value="" disabled>주식을 선택하세요</option>
        <option
          v-for="item in table.items"
          :key="item.id"
          :value="item.stockName"
        >
          {{ item.stockName }}
        </option>
      </select>
    </div>
    <div class="col">
      <InlineInput v-model="stockQuantity" placeholder="주식수량" />
    </div>
    <div class="col d-flex justify-content-start">
      <button
        class="btn btn-sm btn-outline-primary m-1"
        @click="tradeStock('buy')"
      >
        주식 구매
      </button>
      <button
        class="btn btn-sm btn-outline-primary m-1"
        @click="tradeStock('sell')"
      >
        주식 판매
      </button>
    </div>
  </div>
</template>
