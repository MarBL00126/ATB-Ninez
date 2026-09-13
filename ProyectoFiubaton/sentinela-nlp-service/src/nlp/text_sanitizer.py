import re

DNI_PATTERN = re.compile(r"\b\d{1,2}\.?\d{3}\.?\d{3}\b")
EMAIL_PATTERN = re.compile(r"[\w.\-+]+@[\w.\-]+\.[A-Za-z]{2,}")
PHONE_PATTERN = re.compile(r"\b(?:\+?\d[\d\s\-]{7,}\d)\b")
PROPER_NAME_PATTERN = re.compile(r"\b([A-Z][a-z]+(?:\s+[A-Z][a-z]+)+)\b")


def sanitize_text(text: str) -> str:
    sanitized = DNI_PATTERN.sub("[DNI]", text)
    sanitized = EMAIL_PATTERN.sub("[EMAIL]", sanitized)
    sanitized = PHONE_PATTERN.sub("[TELEFONO]", sanitized)
    sanitized = PROPER_NAME_PATTERN.sub("[NOMBRE]", sanitized)
    return re.sub(r"\s+", " ", sanitized).strip()
