from functools import lru_cache
from pathlib import Path

try:
    from pydantic_settings import BaseSettings
except ImportError:  # pragma: no cover
    BaseSettings = object


class Settings(BaseSettings):
    service_name: str = "sentinela-nlp-service"
    port: int = 8002
    chroma_path: Path = Path("chromadb_storage")
    embedding_model_name: str = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"
    observations_collection: str = "observaciones_campo"
    regulations_collection: str = "normativas_rag"

    if BaseSettings is not object:
        model_config = {
            "env_prefix": "SENTINELA_NLP_",
            "env_file": ".env",
            "extra": "ignore",
        }


@lru_cache
def get_settings() -> Settings:
    return Settings()
