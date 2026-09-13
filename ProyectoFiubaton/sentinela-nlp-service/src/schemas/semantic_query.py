from datetime import datetime

from pydantic import BaseModel, ConfigDict, Field


class SemanticQuery(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    token_nnya: str = Field(..., min_length=4, alias="idNnya")
    texto: str = Field(..., min_length=3, alias="observacion")
    desde: datetime | None = None
    hasta: datetime | None = None
    top_k: int = Field(default=5, ge=1, le=20)
