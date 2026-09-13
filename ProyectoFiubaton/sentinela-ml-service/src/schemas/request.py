from typing import Any

from pydantic import BaseModel, ConfigDict, Field


class PredictRequest(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    id_nnya: str = Field(..., min_length=4, alias="idNnya")
    features: dict[str, Any] = Field(default_factory=dict)


class BatchPredictRequest(BaseModel):
    cases: list[PredictRequest] = Field(default_factory=list, max_length=500)
