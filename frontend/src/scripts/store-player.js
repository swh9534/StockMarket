import { reactive } from "vue";

const storage = reactive({
  playerId: "",
  playerMoney: 0,
  playerStockList: [],
});

// localStorage에서 초기값 로드
const initStorage = () => {
  const data = JSON.parse(localStorage.getItem("player") || "{}");
  if (data.playerId) {
    storage.playerId = data.playerId;
    storage.playerMoney = data.playerMoney;
    storage.playerStockList = data.playerStockList || [];
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
