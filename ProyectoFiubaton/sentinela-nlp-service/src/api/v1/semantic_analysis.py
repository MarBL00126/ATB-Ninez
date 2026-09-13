from fastapi import APIRouter, Request

from src.longitudinal.drift_detector import detect_drift
from src.nlp.classification import infer_categories
from src.nlp.embeddings import get_embedding_model
from src.nlp.text_sanitizer import sanitize_text
from src.schemas.response import SemanticResponse
from src.schemas.semantic_query import SemanticQuery
from src.vectorstore.repository import get_repository

router = APIRouter(tags=["semantic-analysis"])


@router.post("/longitudinal-check", response_model=SemanticResponse)
def longitudinal_check(payload: SemanticQuery, request: Request) -> SemanticResponse:
    repository = getattr(request.app.state, "repository", get_repository())
    sanitized = sanitize_text(payload.texto)
    embedding = get_embedding_model().embed(sanitized)
    matches = repository.query_by_token(payload.token_nnya, embedding, payload.top_k)
    similarity, drift_patterns = detect_drift(embedding, matches)
    return SemanticResponse(
        token_nnya=payload.token_nnya,
        similitud_coseno=similarity,
        categorias_inferidas=infer_categories(sanitized),
        patrones_deterioro=drift_patterns,
        matches=[match.text for match in matches],
    )
