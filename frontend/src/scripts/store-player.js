import { reactive } from "vue";

const storage = reactive({
  playerId: "",
  playerMoney: 0,
  playerStockList: [],
});

// localStorage에서 초기값 로드
const initStorage = () => {
  const playerData = JSON.parse(localStorage.getItem("player") || "{}");

  if (playerData.playerId) {
    storage.playerId = playerData.playerId;
    storage.playerMoney = playerData.playerMoney || 0;
    storage.playerStockList = playerData.playerStockList || [];
  } else {
    const currentUser = JSON.parse(localStorage.getItem("currentUser") || "{}");
    if (currentUser.playerId) {
      storage.playerId = currentUser.playerId;
      storage.playerMoney = currentUser.cash || 0;
      storage.playerStockList = currentUser.playerStockList || [];
    }
  }
};

export const usePlayer = () => {
  initStorage();
  return storage;
};

export const storePlayer = (res) => {
  if (!res || res.result !== 0 || !res.data) {
    console.warn("storePlayer: 유효하지 않은 응답", res);
    return;
  }

  const player = res.data;
  storage.playerId = player.playerId;
  storage.playerMoney = player.cash || player.money || 0;
  storage.playerStockList = player.playerStockList || [];
  localStorage.setItem("player", JSON.stringify(storage));
};