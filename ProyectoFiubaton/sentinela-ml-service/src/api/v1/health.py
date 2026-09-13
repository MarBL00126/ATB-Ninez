from fastapi import APIRouter

from config.settings import get_settings
from src.inference.predictor import get_predictor
from src.schemas.response import HealthResponse

router = APIRouter(tags=["health"])


@router.get("/health", response_model=HealthResponse)
def health() -> HealthResponse:
    settings = get_settings()
    predictor = get_predictor()
    return HealthResponse(status="ok", model_loaded=predictor.model_loaded, threshold=settings.risk_threshold)
