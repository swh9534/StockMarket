from fastapi import APIRouter, HTTPException
from services.schemes import StockInput, PredictionOutput, ValidationInput
import torch
import numpy as np
from services.model import StockLSTM, load_model, preprocess_data, calculate_rmse

router = APIRouter(tags=["Inference"])

@router.post("/predict", response_model=PredictionOutput)
async def predict(input_data: StockInput):
    """
    단일 시계열 데이터에 대해 미래 주가를 예측합니다.
    """
    try:
        model = load_model()
        preprocessed_data = preprocess_data(np.array(input_data.sequence))
        
        with torch.no_grad():
            model.eval()
            tensor_input = torch.tensor(preprocessed_data, dtype=torch.float32).unsqueeze(0)
            predictions = model(tensor_input, future_steps=input_data.future_steps)
            predictions = predictions.squeeze().tolist()
            
        return PredictionOutput(predictions=predictions)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"예측 중 오류 발생: {str(e)}")

@router.post("/validate", response_model=PredictionOutput)
async def validate(input_data: ValidationInput):
    """
    실제 주가와 비교하여 RMSE를 계산하고 예측 결과를 반환합니다.
    """
    try:
        model = load_model()
        preprocessed_data = preprocess_data(np.array(input_data.sequence))
        
        with torch.no-grad():
            model.eval()
            tensor_input = torch.tensor(preprocessed_data, dtype=torch.float32).unsqueeze(0)
            predictions = model(tensor_input, future_steps=len(input_data.actual))
            predictions = predictions.squeeze().tolist()
            
            rmse = calculate_rmse(np.array(predictions), np.array(input_data.actual))
            
        return PredictionOutput(predictions=predictions, rmse=rmse)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"검증 중 오류 발생: {str(e)}")