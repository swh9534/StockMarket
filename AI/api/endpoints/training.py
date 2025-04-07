from fastapi import APIRouter, HTTPException
from services.schemes import TrainingInput, TrainingOutput
from services.training import train_model

router = APIRouter(tags=["Training"])

@router.post("/train", response_model=TrainingOutput)
async def train(input_data: TrainingInput):
    """
    LSTM 모델을 학습시킵니다.
    """
    try:
        result = train_model(
            epochs=input_data.epochs,
            learning_rate=input_data.learning_rate,
            batch_size=input_data.batch_size
        )
        return TrainingOutput(**result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"학습 중 오류 발생: {str(e)}")