import torch
import torch.nn as nn
import numpy as np
from typing import List

class StockLSTM(nn.Module):
    def __init__(self, input_size=1, hidden_size=50, num_layers=2, dropout=0.2):
        super(StockLSTM, self).__init__()
        self.hidden_size = hidden_size
        self.num_layers = num_layers
        
        self.lstm = nn.LSTM(input_size, hidden_size, num_layers, 
                          batch_first=True, dropout=dropout)
        self.fc = nn.Linear(hidden_size, 1)
        
    def forward(self, x, future_steps=1):
        batch_size = x.size(0)
        h0 = torch.zeros(self.num_layers, batch_size, self.hidden_size).to(x.device)
        c0 = torch.zeros(self.num_layers, batch_size, self.hidden_size).to(x.device)
        
        out, _ = self.lstm(x, (h0, c0))
        out = self.fc(out[:, -1, :])
        
        if future_steps > 1:
            predictions = [out]
            for _ in range(future_steps - 1):
                out, _ = self.lstm(out.unsqueeze(1), (h0, c0))
                out = self.fc(out[:, -1, :])
                predictions.append(out)
            return torch.cat(predictions, dim=1)
        return out
    
def load_model(model_path: str = "models/stock_lstm.pth") -> StockLSTM:
    """
    학습된 LSTM 모델을 로드합니다.
    """
    model = StockLSTM()
    try:
        model.load_state_dict(torch.load(model_path))
        model.eval()
        return model
    except FileNotFoundError:
        raise Exception(f"모델 파일을 찾을 수 없습니다: {model_path}")

def preprocess_data(data: np.ndarray, seq_length: int = 20) -> np.ndarray:
    """
    주가 데이터를 전처리합니다. (정규화 및 시퀀스 생성)
    """
    data_min, data_max = data.min(), data.max()
    normalized = (data - data_min) / (data_max - data_min + 1e-7)
    
    if len(normalized) < seq_length:
        raise ValueError(f"입력 데이터 길이({len(normalized)})가 최소 시퀀스 길이({seq_length})보다 짧습니다.")
    
    return normalized[-seq_length:].reshape(-1, 1)

def calculate_rmse(predictions: np.ndarray, actual: np.ndarray) -> float:
    """
    예측값과 실제값 간의 RMSE를 계산합니다.
    """
    if len(predictions) != len(actual):
        raise ValueError("예측값과 실제값의 길이가 일치하지 않습니다.")
    mse = np.mean((predictions - actual) ** 2)
    return float(np.sqrt(mse))