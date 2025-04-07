import torch
import torch.nn as nn
import pandas as pd
import numpy as np
from torch.utils.data import DataLoader, TensorDataset
from services.model import StockLSTM, preprocess_data

def train_model(epochs: int, learning_rate: float, batch_size: int) -> dict:
    """
    LSTM 모델을 학습시킵니다.
    """
    # 데이터 로드 (IBM 데이터셋 예시)
    data_path = "data/raw/IBM_2006-01-01_to_2018-01-01.csv"
    df = pd.read_csv(data_path)
    prices = df['Close'].values  # 종가 데이터 사용
    
    # 데이터 전처리
    seq_length = 20
    sequences = []
    targets = []
    for i in range(len(prices) - seq_length):
        sequences.append(prices[i:i + seq_length])
        targets.append(prices[i + seq_length])
    
    sequences = np.array(sequences)
    targets = np.array(targets)
    
    # 정규화
    sequences = preprocess_data(sequences.flatten()).reshape(-1, seq_length, 1)
    targets = (targets - targets.min()) / (targets.max() - targets.min() + 1e-7)
    
    # 데이터셋 생성
    dataset = TensorDataset(
        torch.tensor(sequences, dtype=torch.float32),
        torch.tensor(targets, dtype=torch.float32).unsqueeze(1)
    )
    dataloader = DataLoader(dataset, batch_size=batch_size, shuffle=True)
    
    # 모델 초기화
    model = StockLSTM()
    criterion = nn.MSELoss()
    optimizer = torch.optim.Adam(model.parameters(), lr=learning_rate)
    
    # 학습 루프
    final_loss = 0.0
    for epoch in range(epochs):
        model.train()
        epoch_loss = 0.0
        for batch_seq, batch_target in dataloader:
            optimizer.zero_grad()
            output = model(batch_seq)
            loss = criterion(output, batch_target)
            loss.backward()
            optimizer.step()
            epoch_loss += loss.item()
        
        final_loss = epoch_loss / len(dataloader)
        print(f"Epoch {epoch+1}/{epochs}, Loss: {final_loss}")
    
    # 모델 저장
    torch.save(model.state_dict(), "models/stock_lstm.pth")
    
    return {
        "status": "success",
        "message": "Model training completed successfully",
        "final_loss": final_loss
    }