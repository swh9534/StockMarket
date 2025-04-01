<script setup>
import { usePlayer } from "@/scripts/store-player";
import { onMounted, computed, reactive, ref, nextTick } from "vue";
import { useRouter } from "vue-router";
import apiCall from "@/scripts/api-call";
import { notifySuccess, notifyError } from "@/scripts/store-popups";
import { Pie } from "vue-chartjs";
import { Chart as ChartJS, Title, Tooltip, Legend, ArcElement } from "chart.js";

const router = useRouter();
const stockName = ref("");
const stockQuantity = ref("");
const player = usePlayer();
const totalAssets = ref(0);

ChartJS.register(Title, Tooltip, Legend, ArcElement);

const chartRef = ref(null);
const isDataLoaded = ref(false);

const generateRandomColor = () => {
  const letters = "0123456789ABCDEF";
  let color = "#";
  for (let i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
};

const generateColors = (count) => {
  const colors = [];
  for (let i = 0; i < count; i++) {
    colors.push(generateRandomColor());
  }
  return colors;
};

const chartData = reactive({
  labels: ["현금"],
  datasets: [
    {
      data: [0],
      backgroundColor: ["#36A2EB"],
      hoverBackgroundColor: ["#36A2EB"],
    },
  ],
});

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
};

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

const getPlayerInfo = async () => {
  const playerId = player.currentUser?.playerId || player.playerId;
  if (!playerId) {
    console.warn("playerId가 없습니다. 정보를 가져올 수 없습니다.");
    return;
  }

  // 플레이어 정보 가져오기
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
    const portfolio = portfolioResponse.portfolio || [];
    totalAssets.value = portfolioResponse.totalAssets || 0;
    console.log("포트폴리오 응답:", portfolioResponse);

    if (Array.isArray(portfolio)) {
      table.items = portfolio.map((item) => ({
        id: item.id,
        stockName: String(item.stockName || ""),
        stockPrice: item.stock.stockPrice,
        stockQuantity: item.quantity,
        investedAmount: item.investedAmount,
        totalValue: item.totalValue,
        profitRate: item.profitRate.toFixed(2) + "%",
      }));

      chartData.labels = ["현금"].concat(
        portfolio.map((item) => item.stockName)
      );
      chartData.datasets[0].data = [player.playerMoney].concat(
        portfolio.map((item) => item.totalValue)
      );

      const totalItems = portfolio.length + 1;
      const colors = generateColors(totalItems); // 색상 생성 (아래에서 수정 예정)
      chartData.datasets[0].backgroundColor = colors;
      chartData.datasets[0].hoverBackgroundColor = colors;
    } else {
      chartData.labels = ["현금"];
      chartData.datasets[0].data = [player.playerMoney];
      chartData.datasets[0].backgroundColor = ["#36A2EB"];
      chartData.datasets[0].hoverBackgroundColor = ["#36A2EB"];
    }
  } catch (error) {
    console.error("포트폴리오 정보 가져오기 실패:", error);
    notifyError("주식 목록을 불러오지 못했습니다.");
    chartData.labels = ["현금"];
    chartData.datasets[0].data = [player.playerMoney];
    chartData.datasets[0].backgroundColor = ["#36A2EB"];
    chartData.datasets[0].hoverBackgroundColor = ["#36A2EB"];
  } finally {
    isDataLoaded.value = true;
    await nextTick();
    if (chartRef.value && chartRef.value.chart) {
      chartRef.value.chart.update();
    } else {
      console.warn("차트 인스턴스가 준비되지 않았습니다.");
    }
  }
};

const selectStock = (stock) => {
  stockName.value = stock.stockName; // 객체에서 stockName 사용
};

const filteredStocks = computed(() => {
  return table.items.filter((stock) => {
    const stockNameStr = String(stock.stockName || ""); // 문자열로 강제 변환
    return stockNameStr.toLowerCase().includes(stockName.value.toLowerCase());
  });
});

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

  try {
    const response = await apiCall.post(url, null, data);
    if (response && response.message && response.message.includes("완료")) {
      notifySuccess(`${action === "buy" ? "주식 구매" : "주식 판매"} 성공!`);
      // 입력값 초기화
      stockName.value = "";
      stockQuantity.value = "";
      // 플레이어 정보와 포트폴리오 갱신
      await getPlayerInfo();
      // 차트가 렌더링될 때까지 기다림
      await nextTick();
      if (chartRef.value && chartRef.value.chart) {
        chartRef.value.chart.update();
      } else {
        console.warn("차트 인스턴스가 준비되지 않았습니다.");
      }
    } else {
      notifyError("거래 실패: 서버 응답 오류");
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
  <div class="container-fluid">
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

    <div class="row">
      <div class="col-8">
        <div class="row g-2 align-items-center m-2 mt-0">
          <div class="col-2 d-flex justify-content-end">
            <label class="col-form-label form-control-sm p-1"
              >보유주식목록</label
            >
          </div>
          <div class="col">
            <ItemsTable
              :headers="table.headers"
              :items="table.items"
              :nosetting="true"
              @row-click="selectStock"
            />
          </div>
        </div>
        <div class="row g-2 align-items-center m-2 mt-0">
          <div class="col-2 d-flex justify-content-end">
            <label class="col-form-label form-control-sm p-1">주식선택</label>
          </div>
          <div class="col">
            <input
              v-model="stockName"
              placeholder="주식이름"
              list="stock-names"
              class="form-control form-control-sm"
            />
            <datalist id="stock-names">
              <option
                v-for="stock in filteredStocks"
                :key="stock.stockName"
                :value="stock.stockName"
              ></option>
            </datalist>
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
      </div>

      <div class="col-4">
        <p class="fs-5 fw-bold m-0">총 자산 {{ totalAssets }}원</p>
        <div style="width: 100%; height: 400px">
          <Pie
            v-if="isDataLoaded"
            ref="chartRef"
            :data="chartData"
            :options="chartOptions"
          />
        </div>
      </div>
    </div>
  </div>
</template>
