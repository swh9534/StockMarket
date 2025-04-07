from fastapi import APIRouter
from services.schemes import ModelInfoOutput
from services.model_info import get_model_info

router = APIRouter(tags=["Model Info"])

@router.get("/model-info", response_model=ModelInfoOutput)
async def model_info():
    """
    현재 사용 중인 모델의 정보를 반환합니다.
    """
    info = get_model_info()
    return ModelInfoOutput(**info)