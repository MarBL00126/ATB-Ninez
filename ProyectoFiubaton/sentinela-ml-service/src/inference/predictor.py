from functools import lru_cache

import joblib

from config.settings import get_settings
from src.inference.explainer import RiskExplainer
from src.pipelines.feature_engineering import build_feature_vector
from src.schemas.request import PredictRequest
from src.schemas.response import PredictResponse


class RiskPredictor:
    def __init__(self):
        self.settings = get_settings()
        self.pipeline = self._load_pipeline()
        self.explainer = RiskExplainer()

    @property
    def model_loaded(self) -> bool:
        return self.pipeline is not None

    def predict(self, request: PredictRequest) -> PredictResponse:
        engineered = build_feature_vector(request.features)
        score = self._predict_score(engineered)
        return PredictResponse(
            id_nnya=request.id_nnya,
            risk_score=score,
            threshold_exceeded=score >= self.settings.risk_threshold,
            shap_breakdown=self.explainer.top_k(engineered),
            model_version="xgboost-risk-v1" if self.model_loaded else "heuristic-demo-v1",
        )

    def _load_pipeline(self):
        pipeline_path = self.settings.model_path.parent / "risk_pipeline.joblib"
        if pipeline_path.exists():
            return joblib.load(pipeline_path)
        return None

    def _predict_score(self, engineered: dict[str, float]) -> float:
        if self.pipeline is not None:
            probability = self.pipeline.predict_proba([engineered])[0][1]
            return round(float(probability), 4)
        weights = {
            "ausentismo_rate": 0.14,
            "llamados_102_capped": 0.10,
            "frecuencia_guardia": 0.14,
            "intervencion_reciente": 0.08,
            "desercion": 0.12,
            "lesiones_docentes": 0.16,
            "autolesion_consumo": 0.16,
            "denuncia_vd_hogar": 0.16,
            "habitacional_vulnerable": 0.08,
        }
        weighted = sum(engineered.get(name, 0.0) * weight for name, weight in weights.items())
        return round(max(0.0, min(weighted, 1.0)), 4)


@lru_cache
def get_predictor() -> RiskPredictor:
    return RiskPredictor()
