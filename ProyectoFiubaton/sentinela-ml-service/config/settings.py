from functools import lru_cache
from pathlib import Path

try:
    from pydantic_settings import BaseSettings
except ImportError:  # pragma: no cover
    BaseSettings = object


class Settings(BaseSettings):
    service_name: str = "sentinela-ml-service"
    port: int = 8001
    risk_threshold: float = 0.70
    model_path: Path = Path("models/xgboost_risk_v1.json")
    preprocessor_path: Path = Path("models/preprocessor.joblib")
    shap_explainer_path: Path = Path("models/shap_explainer.joblib")

    if BaseSettings is not object:
        model_config = {
            "env_prefix": "SENTINELA_ML_",
            "env_file": ".env",
            "extra": "ignore",
        }


@lru_cache
def get_settings() -> Settings:
    return Settings()
