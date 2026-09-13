from fastapi.testclient import TestClient

from src.main import app


def test_prediction_api_returns_demo_score():
    with TestClient(app) as client:
        response = client.post(
            "/api/v1/predict",
            json={
                "id_nnya": "NNYA-0001",
                "features": {
                    "ausentismo_dias_ultimo_mes": 12,
                    "lesiones_reportadas_por_docentes": True,
                },
            },
        )

    assert response.status_code == 200
    body = response.json()
    assert body["id_nnya"] == "NNYA-0001"
    assert 0 <= body["risk_score"] <= 1
