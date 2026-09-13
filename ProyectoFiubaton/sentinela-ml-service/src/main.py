from contextlib import asynccontextmanager

from fastapi import FastAPI

from src.api.v1.compat import router as compatibility_router
from src.api.v1.endpoints import router as prediction_router
from src.api.v1.health import router as health_router
from src.inference.predictor import get_predictor


@asynccontextmanager
async def lifespan(app: FastAPI):
    app.state.predictor = get_predictor()
    yield


app = FastAPI(title="Sentinela ML Service", version="0.1.0", lifespan=lifespan)
app.include_router(health_router)
app.include_router(prediction_router, prefix="/api/v1")
app.include_router(compatibility_router)
