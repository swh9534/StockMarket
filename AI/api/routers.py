from fastapi import APIRouter
from api.endpoints import health, inference, info, training

api_router = APIRouter()

api_router.include_router(health.router, prefix="/health")
api_router.include_router(inference.router, prefix="/inference")
api_router.include_router(info.router, prefix="/info")
api_router.include_router(training.router, prefix="/training")