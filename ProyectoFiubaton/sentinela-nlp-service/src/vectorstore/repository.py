from dataclasses import dataclass, field
from functools import lru_cache
from uuid import uuid4

from src.nlp.embeddings import get_embedding_model
from src.schemas.observation import ObservationIn
from src.vectorstore.chroma_client import create_chroma_client
from src.vectorstore.collections import get_collection_names


@dataclass
class VectorMatch:
    id: str
    text: str
    embedding: list[float]
    metadata: dict


@dataclass
class InMemoryVectorRepository:
    records: list[VectorMatch] = field(default_factory=list)

    @property
    def available(self) -> bool:
        return False

    def upsert_observation(self, observation: ObservationIn, sanitized_text: str) -> str:
        embedding = get_embedding_model().embed(sanitized_text)
        record_id = str(uuid4())
        self.records.append(
            VectorMatch(
                id=record_id,
                text=sanitized_text,
                embedding=embedding,
                metadata={
                    "token_nnya": observation.token_nnya,
                    "fecha": observation.fecha.isoformat(),
                    "institucion": observation.institucion,
                    "rol_emisor": observation.rol_emisor,
                },
            )
        )
        return record_id

    def query_by_token(self, token_nnya: str, embedding: list[float], top_k: int = 5) -> list[VectorMatch]:
        return [record for record in self.records if record.metadata.get("token_nnya") == token_nnya][:top_k]


class ChromaVectorRepository(InMemoryVectorRepository):
    def __init__(self):
        super().__init__()
        self.client = create_chroma_client()
        observations_collection, _ = get_collection_names()
        self.collection = self.client.get_or_create_collection(observations_collection) if self.client else None

    @property
    def available(self) -> bool:
        return self.collection is not None

    def upsert_observation(self, observation: ObservationIn, sanitized_text: str) -> str:
        if self.collection is None:
            return super().upsert_observation(observation, sanitized_text)
        embedding = get_embedding_model().embed(sanitized_text)
        record_id = str(uuid4())
        metadata = {
            "token_nnya": observation.token_nnya,
            "fecha": observation.fecha.isoformat(),
            "institucion": observation.institucion,
            "rol_emisor": observation.rol_emisor,
        }
        self.collection.upsert(ids=[record_id], embeddings=[embedding], documents=[sanitized_text], metadatas=[metadata])
        return record_id

    def query_by_token(self, token_nnya: str, embedding: list[float], top_k: int = 5) -> list[VectorMatch]:
        if self.collection is None:
            return super().query_by_token(token_nnya, embedding, top_k)
        result = self.collection.query(
            query_embeddings=[embedding],
            n_results=top_k,
            where={"token_nnya": token_nnya},
            include=["documents", "metadatas", "embeddings"],
        )
        ids = result.get("ids", [[]])[0]
        docs = result.get("documents", [[]])[0]
        metadatas = result.get("metadatas", [[]])[0]
        embeddings = result.get("embeddings", [[]])[0]
        return [
            VectorMatch(id=item_id, text=doc, metadata=metadata, embedding=list(vector))
            for item_id, doc, metadata, vector in zip(ids, docs, metadatas, embeddings)
        ]


@lru_cache
def get_repository() -> InMemoryVectorRepository:
    repo = ChromaVectorRepository()
    return repo if repo.available else InMemoryVectorRepository()
