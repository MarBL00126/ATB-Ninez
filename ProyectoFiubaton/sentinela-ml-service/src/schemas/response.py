from pydantic import BaseModel


class ShapValue(BaseModel):
    feature: str
    value: float


class PredictResponse(BaseModel):
    id_nnya: str
    risk_score: float
    threshold_exceeded: bool
    shap_breakdown: list[ShapValue]
    model_version: str


class HealthResponse(BaseModel):
    status: str
    model_loaded: bool
    threshold: float


class JavaPredictResponse(BaseModel):
    scoreRiesgo: float
    shapValues: dict[str, float]
