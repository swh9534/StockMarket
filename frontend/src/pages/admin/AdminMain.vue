<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "@/api";
import { notifyError, showSpinner, hideSpinner } from "@/scripts/store-popups";

const router = useRouter(); // 라우터 사용
const stockName = ref("");
const initialPrice = ref("");
const players = ref([]);

const addStock = async () => {
  if (!stockName.value || !initialPrice.value) {
    notifyError("주식 이름과 가격을 입력하세요.");
    return;
  }

  const price = parseInt(initialPrice.value, 10);
  if (isNaN(price) || price <= 0) {
    notifyError("주식 가격은 0보다 큰 정수여야 합니다.");
    return;
  }

  try {
    showSpinner();
    const requestData = {
      stockName: stockName.value,
      initialPrice: price,
    };
    console.log("Sending request data:", requestData);
    const response = await api.post("/stocks/add", requestData);
    console.log("Stock added:", response.data);
    stockName.value = "";
    initialPrice.value = "";
    notifyError(
      `주식이 성공적으로 추가되었습니다: ${response.data.stockName} (가격: ${response.data.stockPrice})`
    );
  } catch (error) {
    console.error("Failed to add stock:", error);
    if (error.response) {
      console.error("Error response:", error.response);
      notifyError(
        `주식 추가 실패: ${error.response.data?.message || error.message}`
      );
    } else {
      notifyError(`주식 추가 실패: ${error.message}`);
    }
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

// 로그아웃 함수
const logout = async () => {
  try {
    showSpinner();
    await api.post("/users/logout"); // 로그아웃 API 호출
    // localStorage 초기화
    localStorage.removeItem("currentUser");
    localStorage.removeItem("userId");
    localStorage.removeItem("playerId");
    localStorage.removeItem("isAdmin");
    // 루트 경로로 리다이렉트
    router.push("/");
  } catch (error) {
    console.error("Failed to logout:", error);
    if (error.response) {
      notifyError(
        `로그아웃 실패: ${error.response.data?.message || error.message}`
      );
    } else {
      notifyError(`로그아웃 실패: ${error.message}`);
    }
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
    <!-- 로그아웃 버튼 -->
    <div class="d-flex justify-content-end mb-3">
      <button class="btn btn-danger" @click="logout">로그아웃</button>
    </div>

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
              v-model="initialPrice"
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
