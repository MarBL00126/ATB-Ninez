from src.schemas.response import ShapValue


class RiskExplainer:
    def __init__(self, explainer=None):
        self.explainer = explainer

    def top_k(self, features: dict[str, float], k: int = 5) -> list[ShapValue]:
        ranked = sorted(features.items(), key=lambda item: abs(float(item[1])), reverse=True)
        return [ShapValue(feature=name, value=round(float(value), 4)) for name, value in ranked[:k]]
