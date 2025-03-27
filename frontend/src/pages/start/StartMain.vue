<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import apiCall from "@/scripts/api-call";
import { notifyInfo, notifyError } from "@/scripts/store-popups";
import InlineInput from "@/components/InlineInput.vue";

const router = useRouter();

const playerId = ref("");
const playerMoney = ref("");
const isNewPlayer = ref(false);

const storePlayer = (player) => {
  localStorage.setItem("player", JSON.stringify(player));
};

const start = async () => {
  const url = "/api/players/" + playerId.value;
  const response = await apiCall.justGet(url, null, null);
  if (response.result === apiCall.Response.SUCCESS) {
    storePlayer(response.body);
    router.push("/stock");
  } else {
    isNewPlayer.value = true;
    notifyInfo("초기투자금을 입력후 [플레이어 등록] 버튼을 누르세요.");
  }
};

const signup = async () => {
  if (!playerId.value || !playerMoney.value) {
    notifyError("플레이어ID와 초기투자금을 모두 입력하세요.");
    return;
  }

  const url = "/api/players";
  const playerData = {
    playerId: playerId.value,
    playerMoney: parseInt(playerMoney.value, 10),
  };

  const response = await apiCall.post(url, null, playerData);
  if (response.result === apiCall.Response.SUCCESS) {
    storePlayer(response.body);
    isNewPlayer.value = false;
    router.push("/stock");
  }
};
</script>

<template>
  <div class="container-sm mt-3 border border-2 p-1" style="max-width: 600px">
    <div class="bss-background p-1">
      <template v-if="isNewPlayer">
        <div class="mt-3 d-flex justify-content-center" style="height: 230px">
          <span class="text-center text-danger fs-1 fw-bold mt-4"
            >SKALA STOCK Market</span
          >
        </div>
        <div class="row bg-info-subtle p-2 m-1" style="opacity: 92%">
          <div class="col">
            <InlineInput
              label="플레이어ID"
              class="mb-1"
              v-model="playerId"
              type="text"
              placeholder="플레이어ID 입력"
            />
            <InlineInput
              label="초기투자금"
              class="mb-1"
              v-model="playerMoney"
              type="text"
              placeholder="초기투자금 입력"
            />
          </div>
          <div class="d-flex justify-content-end">
            <button
              v-if="isNewPlayer"
              class="btn btn-primary btn-sm"
              @click="signup"
            >
              플레이어 등록
            </button>
          </div>
        </div>
      </template>
      <template v-else>
        <div class="mt-3 d-flex justify-content-center" style="height: 270px">
          <span class="text-center text-danger fs-1 fw-bold mt-4"
            >SKALA STOCK Market</span
          >
        </div>
        <div class="row bg-info-subtle p-2 m-1" style="opacity: 92%">
          <div class="col">
            <InlineInput
              label="플레이어ID"
              class="mb-1"
              v-model="playerId"
              type="text"
              placeholder="플레이어ID 입력"
              @input-enter-pressed="start"
            />
          </div>
          <div class="d-flex justify-content-end">
            <button class="btn btn-primary btn-sm" @click="start">시작</button>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.bss-background {
  width: 590px;
  height: 380px;
  background-image: url("/logo.png");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
</style>
