from src.nlp.embeddings import cosine_similarity
from src.vectorstore.repository import VectorMatch


def detect_drift(current_embedding: list[float], history: list[VectorMatch]) -> tuple[float, list[str]]:
    if not history:
        return 0.0, ["sin_historial"]
    similarities = [cosine_similarity(current_embedding, item.embedding) for item in history]
    average = sum(similarities) / len(similarities)
    patterns = []
    if average < 0.55:
        patterns.append("cambio_semantico_relevante")
    if len(history) >= 3 and average < 0.70:
        patterns.append("deterioro_longitudinal_posible")
    return round(average, 4), patterns or ["sin_deterioro_detectado"]
