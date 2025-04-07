from pydantic import BaseModel
from typing import List, Optional

# 예측 요청을 위한 입력 스키마
class StockInput(BaseModel):
    sequence: List[float]  # 과거 주가 데이터
    future_steps: int = 1  # 예측할 미래 스텝 수

# 예측 응답을 위한 출력 스키마
class PredictionOutput(BaseModel):
    predictions: List[float]  # 예측된 주가 리스트
    rmse: Optional[float] = None  # RMSE 값 (실제값과 비교 시)

# 검증 요청을 위한 입력 스키마
class ValidationInput(BaseModel):
    sequence: List[float]  # 입력 시계열 데이터
    actual: List[float]   # 실제 주가 데이터 (RMSE 계산용)

# 학습 요청을 위한 입력 스키마
class TrainingInput(BaseModel):
    epochs: int = 10
    learning_rate: float = 0.001
    batch_size: int = 32

# 학습 응답을 위한 출력 스키마
class TrainingOutput(BaseModel):
    status: str
    message: str
    final_loss: float

# 모델 정보 응답을 위한 출력 스키마
class ModelInfoOutput(BaseModel):
    model_name: str
    input_size: int
    hidden_size: int
    num_layers: int
    dropout: float
    description: str

# 헬스 체크 응답을 위한 출력 스키마
class HealthCheckOutput(BaseModel):
    status: str
    message: str