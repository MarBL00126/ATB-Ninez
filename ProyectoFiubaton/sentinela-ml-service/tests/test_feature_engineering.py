from src.pipelines.feature_engineering import build_feature_vector


def test_feature_engineering_caps_counts():
    vector = build_feature_vector(
        {
            "ausentismo_dias_ultimo_mes": 30,
            "cantidad_llamados_previos_linea102": 7,
            "meses_desde_ultima_intervencion": 3,
        }
    )

    assert vector["ausentismo_rate"] == 1.0
    assert vector["llamados_102_capped"] == 3.0
    assert vector["intervencion_reciente"] == 1.0
