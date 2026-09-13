from fastapi import APIRouter, Request

from src.nlp.text_sanitizer import sanitize_text
from src.schemas.observation import ObservationIn
from src.schemas.response import ObservationResponse
from src.vectorstore.repository import get_repository

router = APIRouter(tags=["observations"])


@router.post("/observations", response_model=ObservationResponse)
def create_observation(payload: ObservationIn, request: Request) -> ObservationResponse:
    repository = getattr(request.app.state, "repository", get_repository())
    sanitized = sanitize_text(payload.texto)
    record_id = repository.upsert_observation(payload, sanitized)
    return ObservationResponse(id=record_id, token_nnya=payload.token_nnya, sanitized_text=sanitized)
