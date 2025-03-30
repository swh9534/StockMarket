<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "@/api";
import {
  notifyInfo,
  notifyError,
  showSpinner,
  hideSpinner,
} from "@/scripts/store-popups";
import InlineInput from "@/components/InlineInput.vue";

const router = useRouter();

const userId = ref("");
const password = ref("");
const isNewUser = ref(false);
const playerName = ref("");
const initialCash = ref("");
const isProcessing = ref(false);

onMounted(() => {
  const storedUser = localStorage.getItem("currentUser");
  if (storedUser && router.currentRoute.value.path === "/") {
    router.push("/stock");
  }
});

const storeUser = (user) => {
  console.log("User stored:", user); // 로그 추가
  localStorage.setItem("currentUser", JSON.stringify(user));
  localStorage.setItem("userId", user.userId);
  localStorage.setItem("playerId", user.playerId);
};

const checkAdmin = async () => {
  try {
    console.log("Checking admin for user:", userId.value); // 로그 추가
    const response = await api.post("/users/admin/check", {
      userId: userId.value,
      password: password.value,
    });
    console.log("Admin check response:", response.data); // 로그 추가
    localStorage.setItem("isAdmin", response.data?.isAdmin ? "true" : "false");
  } catch (error) {
    console.error("Admin check failed:", error); // 오류 로그 추가
    localStorage.setItem("isAdmin", "false");
  }
};

const login = async () => {
  if (isProcessing.value || !userId.value || !password.value) {
    notifyError("ID와 비밀번호를 입력하세요.");
    return;
  }

  try {
    console.log("Logging in with userId:", userId.value); // 로그 추가
    isProcessing.value = true;
    showSpinner();

    const response = await api.post("/users/login", {
      userId: userId.value,
      password: password.value,
    });

    console.log("Login response:", response); // 로그인 응답 로그 추가

    if (response.status === 200) {
      storeUser(response.data);
      // await checkAdmin();
      router.push("/stock");
    }
  } catch (error) {
    console.error("Login failed:", error); // 로그인 실패 로그 추가
    isNewUser.value = true;
    notifyInfo("새로운 사용자입니다. 회원가입 정보를 입력하세요.");
  } finally {
    isProcessing.value = false;
    hideSpinner();
  }
};

const signup = async () => {
  if (
    isProcessing.value ||
    !userId.value ||
    !password.value ||
    !playerName.value ||
    !initialCash.value
  ) {
    notifyError("모든 필드를 입력하세요.");
    return;
  }

  try {
    console.log("Signing up new user:", userId.value); // 로그 추가
    isProcessing.value = true;
    showSpinner();

    const response = await api.post("/users/register", {
      userId: userId.value,
      password: password.value,
      playerName: playerName.value,
      initialCash: parseInt(initialCash.value, 10),
    });

    console.log("Signup response:", response); // 회원가입 응답 로그 추가

    storeUser(response.data);
    await checkAdmin();
    isNewUser.value = false;
    router.push("/stock");
  } catch (error) {
    console.error("Signup failed:", error); // 회원가입 실패 로그 추가
    notifyError("회원가입 실패: " + error.message);
  } finally {
    isProcessing.value = false;
    hideSpinner();
  }
};
</script>

<template>
  <div class="container-sm mt-3 border border-2 p-1" style="max-width: 600px">
    <div class="bss-background p-1">
      <template v-if="isNewUser">
        <div class="mt-3 d-flex justify-content-center" style="height: 230px">
          <span class="text-center text-danger fs-1 fw-bold mt-4">
            SKALA STOCK Market
          </span>
        </div>
        <div class="row bg-info-subtle p-2 m-1" style="opacity: 92%">
          <div class="col">
            <InlineInput
              label="ID"
              class="mb-1"
              v-model="userId"
              type="text"
              placeholder="ID 입력"
            />
            <InlineInput
              label="비밀번호"
              class="mb-1"
              v-model="password"
              type="password"
              placeholder="비밀번호 입력"
            />
            <InlineInput
              label="이름"
              class="mb-1"
              v-model="playerName"
              type="text"
              placeholder="이름 입력"
            />
            <InlineInput
              label="초기투자금"
              class="mb-1"
              v-model="initialCash"
              type="text"
              placeholder="초기투자금 입력"
            />
          </div>
          <div class="d-flex justify-content-end">
            <button
              class="btn btn-primary btn-sm"
              @click="signup"
              :disabled="isProcessing"
            >
              회원가입
            </button>
          </div>
        </div>
      </template>

      <template v-else>
        <div class="mt-3 d-flex justify-content-center" style="height: 270px">
          <span class="text-center text-danger fs-1 fw-bold mt-4">
            SKALA STOCK Market
          </span>
        </div>
        <div class="row bg-info-subtle p-2 m-1" style="opacity: 92%">
          <div class="col">
            <InlineInput
              label="ID"
              class="mb-1"
              v-model="userId"
              type="text"
              placeholder="ID 입력"
              @input-enter-pressed="login"
            />
            <InlineInput
              label="비밀번호"
              class="mb-1"
              v-model="password"
              type="password"
              placeholder="비밀번호 입력"
              @input-enter-pressed="login"
            />
          </div>
          <div class="d-flex justify-content-end">
            <button
              class="btn btn-primary btn-sm"
              @click="login"
              :disabled="isProcessing"
            >
              로그인
            </button>
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
