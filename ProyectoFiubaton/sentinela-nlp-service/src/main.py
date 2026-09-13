from contextlib import asynccontextmanager

from fastapi import FastAPI

from src.api.v1.compat import router as compatibility_router
from src.api.v1.health import router as health_router
from src.api.v1.observations import router as observations_router
from src.api.v1.semantic_analysis import router as semantic_router
from src.vectorstore.repository import get_repository


@asynccontextmanager
async def lifespan(app: FastAPI):
    app.state.repository = get_repository()
    yield


app = FastAPI(title="Sentinela NLP Service", version="0.1.0", lifespan=lifespan)
app.include_router(health_router)
app.include_router(observations_router, prefix="/api/v1")
app.include_router(semantic_router, prefix="/api/v1")
app.include_router(compatibility_router)
