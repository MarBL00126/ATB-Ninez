from functools import lru_cache
from hashlib import sha256
from math import sqrt

from config.settings import get_settings


class EmbeddingModel:
    def __init__(self):
        self.settings = get_settings()
        self.model = self._load_model()

    @property
    def loaded(self) -> bool:
        return self.model is not None

    def embed(self, text: str) -> list[float]:
        if self.model is not None:
            return self.model.encode(text, normalize_embeddings=True).tolist()
        return _hash_embedding(text)

    def _load_model(self):
        try:
            from sentence_transformers import SentenceTransformer

            return SentenceTransformer(self.settings.embedding_model_name)
        except Exception:
            return None


def cosine_similarity(left: list[float], right: list[float]) -> float:
    numerator = sum(a * b for a, b in zip(left, right))
    left_norm = sqrt(sum(a * a for a in left))
    right_norm = sqrt(sum(b * b for b in right))
    if left_norm == 0 or right_norm == 0:
        return 0.0
    return round(numerator / (left_norm * right_norm), 4)


def _hash_embedding(text: str, dimensions: int = 32) -> list[float]:
    digest = sha256(text.lower().encode("utf-8")).digest()
    values = [(digest[index % len(digest)] / 255.0) for index in range(dimensions)]
    norm = sqrt(sum(value * value for value in values)) or 1.0
    return [value / norm for value in values]


@lru_cache
def get_embedding_model() -> EmbeddingModel:
    return EmbeddingModel()
