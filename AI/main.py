from fastapi import FastAPI
from api.routers import api_router

app = FastAPI(
    title="Stock Price Prediction API",
    description="API for stock price prediction using LSTM model.",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_url="/openapi.json",
)
# 라우터 등록

app.include_router(api_router, prefix="/api", tags=["API"])