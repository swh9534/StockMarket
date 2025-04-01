<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import api from "@/api"; // API 호출 유틸리티
import { notifyError, showSpinner, hideSpinner } from "@/scripts/store-popups"; // 팝업 유틸리티
import PlayerStocks from "./components/PlayerStocks.vue";
import StockList from "./components/StockList.vue";
import StockGraph from "./components/StockGraph.vue";

const router = useRouter(); // 라우터 사용
const playerId = ref("");
const selectedStock = ref(null); // 선택된 주식
const key = ref(0); // StockGraph 컴포넌트를 강제로 리렌더링하기 위한 키

const handleSelectStock = (stock) => {
  selectedStock.value = stock; // 선택된 주식 업데이트
  // 주식이 변경되면 컴포넌트 키를 증가시켜 강제 리렌더링
  key.value++;
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
  const currentUser = JSON.parse(localStorage.getItem("currentUser") || "{}");
  if (currentUser?.playerId) {
    playerId.value = currentUser.playerId;
  }
});
</script>

<template>
  <div class="container-fluid">
    <div class="row bg-body-tertiary align-items-center">
      <div class="col">
        <span class="ps-2 fs-2">SKALA STOCK Market</span>
      </div>
      <!-- 로그아웃 버튼 -->
      <div class="col-auto pe-2">
        <button class="btn btn-danger" @click="logout">로그아웃</button>
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
            :key="key"
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
