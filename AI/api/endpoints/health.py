from fastapi import APIRouter
from services.schemes import HealthCheckOutput

router = APIRouter(tags=["Health Check"])

@router.get("/health", response_model=HealthCheckOutput)
async def health_check():
    """
    API 서버의 상태를 확인합니다.
    """
    return HealthCheckOutput(status="healthy", message="API is running smoothly!")