from datetime import datetime

from pydantic import BaseModel, Field


class ObservationIn(BaseModel):
    token_nnya: str = Field(..., min_length=4)
    texto: str = Field(..., min_length=3)
    fecha: datetime
    institucion: str
    rol_emisor: str
