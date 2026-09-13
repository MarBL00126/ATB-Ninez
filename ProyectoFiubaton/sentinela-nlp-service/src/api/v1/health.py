from fastapi import APIRouter

from config.settings import get_settings
from src.nlp.embeddings import get_embedding_model
from src.schemas.response import HealthResponse
from src.vectorstore.repository import get_repository

router = APIRouter(tags=["health"])


@router.get("/health", response_model=HealthResponse)
def health() -> HealthResponse:
    settings = get_settings()
    return HealthResponse(
        status="ok",
        vectorstore_available=get_repository().available,
        embedding_model=settings.embedding_model_name if get_embedding_model().loaded else "hash-demo-v1",
    )
