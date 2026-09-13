from fastapi import APIRouter, Request

from src.inference.predictor import get_predictor
from src.schemas.request import PredictRequest
from src.schemas.response import JavaPredictResponse

router = APIRouter(tags=["compatibility"])


@router.post("/predict", response_model=JavaPredictResponse)
def predict_java_contract(payload: PredictRequest, request: Request) -> JavaPredictResponse:
    predictor = getattr(request.app.state, "predictor", get_predictor())
    result = predictor.predict(payload)
    return JavaPredictResponse(
        scoreRiesgo=result.risk_score,
        shapValues={item.feature: item.value for item in result.shap_breakdown},
    )
