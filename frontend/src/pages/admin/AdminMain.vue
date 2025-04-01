<script setup>
import { ref, onMounted } from "vue";
import api from "@/scripts/api-call"; // API 호출 유틸리티
import { notifyError, showSpinner, hideSpinner } from "@/scripts/store-popups";

const stockName = ref("");
const stockPrice = ref("");
const players = ref([]);

const addStock = async () => {
  if (!stockName.value || !stockPrice.value) {
    notifyError("주식 이름과 가격을 입력하세요.");
    return;
  }

  try {
    showSpinner();
    const response = await api.post("/stocks/add", {
      name: stockName.value,
      price: parseFloat(stockPrice.value),
    });
    console.log("Stock added:", response.data);
    stockName.value = "";
    stockPrice.value = "";
    notifyInfo("주식이 성공적으로 추가되었습니다.");
  } catch (error) {
    console.error("Failed to add stock:", error);
    notifyError("주식 추가 실패: " + error.message);
  } finally {
    hideSpinner();
  }
};

const fetchPlayers = async () => {
  try {
    showSpinner();
    const response = await api.get("/users/all");
    players.value = response.data;
  } catch (error) {
    console.error("Failed to fetch players:", error);
    notifyError("플레이어 조회 실패: " + error.message);
  } finally {
    hideSpinner();
  }
};

onMounted(() => {
  fetchPlayers(); // 페이지 로드 시 플레이어 목록 조회
});
</script>

<template>
  <div class="container mt-3">
    <h2>관리자 페이지</h2>

    <!-- 주식 추가 -->
    <div class="card mb-3">
      <div class="card-body">
        <h5 class="card-title">주식 추가</h5>
        <div class="row">
          <div class="col">
            <input
              v-model="stockName"
              type="text"
              class="form-control mb-2"
              placeholder="주식 이름"
            />
            <input
              v-model="stockPrice"
              type="number"
              class="form-control mb-2"
              placeholder="주식 가격"
            />
          </div>
          <div class="col-auto">
            <button class="btn btn-primary" @click="addStock">추가</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 플레이어 조회 -->
    <div class="card">
      <div class="card-body">
        <h5 class="card-title">모든 플레이어</h5>
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>이름</th>
              <th>자산</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="player in players" :key="player.userId">
              <td>{{ player.userId }}</td>
              <td>{{ player.playerName }}</td>
              <td>{{ player.initialCash }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
