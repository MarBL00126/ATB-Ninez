from datetime import datetime

from src.schemas.observation import ObservationIn
from src.vectorstore.repository import InMemoryVectorRepository


def test_repository_stores_and_filters_by_token():
    repository = InMemoryVectorRepository()
    observation = ObservationIn(
        token_nnya="NNYA-0001",
        texto="observacion de prueba",
        fecha=datetime(2026, 1, 1),
        institucion="escuela",
        rol_emisor="orientador",
    )

    repository.upsert_observation(observation, "observacion de prueba")
    matches = repository.query_by_token("NNYA-0001", [0.1, 0.2], top_k=3)

    assert len(matches) == 1
    assert matches[0].metadata["institucion"] == "escuela"
