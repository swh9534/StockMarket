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
      storage.playerStockList = [];
    }
  }
};

export const usePlayer = () => {
  initStorage();
  return storage;
};
export const storePlayer = (player) => {
  storage.playerId = player.playerId;
  storage.playerMoney = player.money || player.playerMoney || 0; // API 응답 필드명 대응
  storage.playerStockList = player.playerStockList || [];
  localStorage.setItem("player", JSON.stringify(storage));
};
