def _as_number(value, default: float = 0.0) -> float:
    if isinstance(value, bool):
        return 1.0 if value else 0.0
    if value is None or value == "":
        return default
    try:
        return float(value)
    except (TypeError, ValueError):
        return default


def build_feature_vector(features: dict) -> dict[str, float]:
    """Reusable feature recipe shared by training and inference."""
    ausentismo = _as_number(features.get("ausentismo_dias_ultimo_mes"))
    llamados = _as_number(features.get("cantidad_llamados_previos_linea102"))
    meses = _as_number(features.get("meses_desde_ultima_intervencion"), default=24.0)
    guardia = _as_number(features.get("consultas_guardia_lesiones_pococlaras_ult12m"))

    return {
        "ausentismo_rate": min(ausentismo / 20.0, 1.0),
        "llamados_102_capped": min(llamados, 3.0),
        "frecuencia_guardia": min(guardia / 4.0, 1.0),
        "intervencion_reciente": 1.0 if meses <= 6 else 0.0,
        "desercion": _as_number(features.get("desercion_o_abandono_intermitente")),
        "lesiones_docentes": _as_number(features.get("lesiones_reportadas_por_docentes")),
        "autolesion_consumo": _as_number(features.get("atencion_por_autolesion_o_consumo")),
        "denuncia_vd_hogar": _as_number(features.get("denuncia_violencia_domestica_en_el_hogar")),
        "habitacional_vulnerable": 1.0
        if str(features.get("situacion_habitacional_reportada", "")).lower() in {"hacinamiento", "sin techo", "precaria"}
        else 0.0,
    }
