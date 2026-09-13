from fastapi import APIRouter, Request

from src.inference.predictor import get_predictor
from src.schemas.request import BatchPredictRequest, PredictRequest
from src.schemas.response import PredictResponse

router = APIRouter(tags=["prediction"])


@router.post("/predict", response_model=PredictResponse)
def predict(payload: PredictRequest, request: Request) -> PredictResponse:
    predictor = getattr(request.app.state, "predictor", get_predictor())
    return predictor.predict(payload)


@router.post("/batch-predict", response_model=list[PredictResponse])
def batch_predict(payload: BatchPredictRequest, request: Request) -> list[PredictResponse]:
    predictor = getattr(request.app.state, "predictor", get_predictor())
    return [predictor.predict(case) for case in payload.cases]
