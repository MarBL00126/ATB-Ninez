from fastapi import APIRouter, Request

from src.api.v1.semantic_analysis import longitudinal_check
from src.schemas.response import JavaSemanticResponse
from src.schemas.semantic_query import SemanticQuery

router = APIRouter(tags=["compatibility"])


@router.post("/embeddings", response_model=JavaSemanticResponse)
def embeddings_java_contract(payload: SemanticQuery, request: Request) -> JavaSemanticResponse:
    result = longitudinal_check(payload, request)
    return JavaSemanticResponse(
        similitudLongitudinal=result.similitud_coseno,
        categoriasDetectadas=result.categorias_inferidas,
    )
