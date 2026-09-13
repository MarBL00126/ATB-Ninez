from src.nlp.text_sanitizer import sanitize_text


def test_sanitizer_removes_identifiers():
    text = sanitize_text("Juan Perez DNI 12.345.678 escribe a jp@example.com")

    assert "Juan Perez" not in text
    assert "12.345.678" not in text
    assert "jp@example.com" not in text
    assert "[NOMBRE]" in text
    assert "[DNI]" in text
