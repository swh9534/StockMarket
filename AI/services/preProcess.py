import numpy as np

def preprocess_data(data: np.ndarray, seq_length: int = 20) -> np.ndarray:
    """
    주가 데이터를 전처리합니다. (정규화 및 시퀀스 생성)
    """
    data_min, data_max = data.min(), data.max()
    normalized = (data - data_min) / (data_max - data_min + 1e-7)
    
    if len(normalized) < seq_length:
        raise ValueError(f"입력 데이터 길이({len(normalized)})가 최소 시퀀스 길이({seq_length})보다 짧습니다.")
    
    return normalized[-seq_length:].reshape(-1, 1)