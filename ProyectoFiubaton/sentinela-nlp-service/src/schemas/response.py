from pydantic import BaseModel


class ObservationResponse(BaseModel):
    id: str
    token_nnya: str
    sanitized_text: str


class SemanticResponse(BaseModel):
    token_nnya: str
    similitud_coseno: float
    categorias_inferidas: list[str]
    patrones_deterioro: list[str]
    matches: list[str]


class HealthResponse(BaseModel):
    status: str
    vectorstore_available: bool
    embedding_model: str


class JavaSemanticResponse(BaseModel):
    similitudLongitudinal: float
    categoriasDetectadas: list[str]
