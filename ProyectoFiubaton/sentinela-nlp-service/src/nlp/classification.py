KEYWORDS = {
    "aislamiento": {"aislado", "aislamiento", "solo", "retirado", "no participa"},
    "violencia_fisica": {"golpe", "lesion", "moreton", "violencia", "agresion"},
    "negligencia": {"abandono", "higiene", "hambre", "falta de cuidado", "controles atrasados"},
    "salud_mental": {"autolesion", "tristeza", "ansiedad", "consumo", "crisis"},
    "ausentismo": {"ausencia", "faltas", "ausentismo", "abandono escolar"},
}


def infer_categories(text: str) -> list[str]:
    lowered = text.lower()
    categories = [category for category, words in KEYWORDS.items() if any(word in lowered for word in words)]
    return categories or ["sin_categoria_clara"]
