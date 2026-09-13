const DEFAULT_TOKEN = "Bearer demo:ADMIN,AUDITOR,TRABAJADOR_SOCIAL:Quilmes";

const state = {
  token: localStorage.getItem("sentinela-token") || DEFAULT_TOKEN,
  legajos: [],
  alertas: [],
  selectedCaseId: null,
  filter: "",
};

const demoLegajos = [
  { idNnya: "NNYA-0001", edad: 8, sexo: "F", localidadPartido: "Quilmes", fechaAltaLegajo: todayOffset(-300) },
  { idNnya: "NNYA-0002", edad: 14, sexo: "M", localidadPartido: "Moreno", fechaAltaLegajo: todayOffset(-120) },
  { idNnya: "NNYA-0003", edad: 5, sexo: "X", localidadPartido: "San Martin", fechaAltaLegajo: todayOffset(-48) },
];

const demoAlertas = [
  {
    id: 1,
    idNnya: "NNYA-0001",
    fechaCreacion: new Date().toISOString(),
    scoreRiesgo: 0.86,
    explicacion: "Ausentismo alto, intervencion reciente y reporte escolar concurrente.",
    estado: "PENDIENTE",
  },
  {
    id: 2,
    idNnya: "NNYA-0002",
    fechaCreacion: new Date(Date.now() - 36 * 60 * 60 * 1000).toISOString(),
    scoreRiesgo: 0.74,
    explicacion: "Llamados 102 recurrentes y deterioro longitudinal posible.",
    estado: "EN_REVISION",
  },
];

document.addEventListener("DOMContentLoaded", () => {
  byId("auth-token").value = state.token;
  bindNavigation();
  bindForms();
  loadAll();
});

function bindNavigation() {
  document.querySelectorAll("[data-tab]").forEach((button) => {
    button.addEventListener("click", () => activateTab(button.dataset.tab));
  });
  byId("refresh-btn").addEventListener("click", loadAll);
  byId("global-search").addEventListener("input", (event) => {
    state.filter = event.target.value.trim().toLowerCase();
    renderAll();
  });
  byId("auth-token").addEventListener("change", (event) => {
    state.token = event.target.value.trim() || DEFAULT_TOKEN;
    localStorage.setItem("sentinela-token", state.token);
    loadAll();
  });
  byId("alert-filter").addEventListener("change", () => loadAlertas());
  byId("new-case-btn").addEventListener("click", () => {
    state.selectedCaseId = null;
    byId("case-form").reset();
    byId("case-form").elements.fechaAltaLegajo.value = todayOffset(0);
    byId("selected-case-chip").textContent = "Nuevo";
  });
  loadVectorHealth();
}

function bindForms() {
  byId("case-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = Object.fromEntries(new FormData(form).entries());
    payload.edad = Number(payload.edad);
    const saved = await apiJson("/api/v1/legajos", {
      method: "POST",
      body: JSON.stringify(payload),
    });
    upsertLocal(state.legajos, saved || payload, "idNnya");
    state.selectedCaseId = (saved || payload).idNnya;
    renderAll("Guardado");
  });

  byId("intervention-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const idNnya = state.selectedCaseId || byId("case-form").elements.idNnya.value;
    if (!idNnya) {
      renderStatus("Seleccione un legajo");
      return;
    }
    const payload = Object.fromEntries(new FormData(event.currentTarget).entries());
    await apiJson(`/api/v1/intervenciones/${encodeURIComponent(idNnya)}`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
    event.currentTarget.reset();
    event.currentTarget.elements.profesional.value = "equipo-demo";
    renderStatus("Intervencion registrada");
  });

  byId("emergency-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = Object.fromEntries(new FormData(form).entries());
    payload.cantidadLlamadosPreviosLinea102 = Number(payload.cantidadLlamadosPreviosLinea102 || 0);
    payload.mesesDesdeUltimaIntervencion = Number(payload.mesesDesdeUltimaIntervencion || 0);
    payload.legajoPrevioEnRunna = payload.legajoPrevioEnRunna === "true";
    payload.origenLlamadoPredominante = payload.tipoLlamado;
    await apiJson("/api/v1/ingesta/linea-102", {
      method: "POST",
      body: JSON.stringify(payload),
    });
    renderStatus(`Ficha ${payload.lineaOrigen} registrada`);
    const matchingCase = state.legajos.find((legajo) => legajo.idNnya === payload.idNnya);
    if (!matchingCase) {
      state.legajos.unshift({
        idNnya: payload.idNnya,
        edad: 0,
        sexo: "X",
        localidadPartido: "Sin asignar",
        fechaAltaLegajo: todayOffset(0),
      });
      renderAll();
    }
  });

  byId("evaluation-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const data = Object.fromEntries(new FormData(form).entries());
    const features = collectEvaluationFeatures(form);
    const payload = { idNnya: data.idNnya, features, observacion: data.observacion };
    const response = await apiJson("/api/v1/evaluaciones", {
      method: "POST",
      body: JSON.stringify(payload),
    });
    const result = response || localEvaluation(payload);
    renderEvaluation(result);
    if (result.idNnya && result.scoreRiesgo >= 0.7) {
      upsertLocal(state.alertas, {
        id: result.id || Date.now(),
        idNnya: result.idNnya,
        fechaCreacion: new Date().toISOString(),
        scoreRiesgo: result.scoreRiesgo,
        explicacion: result.explicacion,
        estado: result.estado || "PENDIENTE",
      }, "id");
      renderAll();
    }
  });

  byId("paper-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = Object.fromEntries(new FormData(form).entries());
    payload.year = payload.year ? Number(payload.year) : null;
    const result = await apiJson("/api/v1/biblioteca/papers", {
      method: "POST",
      body: JSON.stringify(payload),
    });
    if (result) {
      renderStatus(`${result.chunksCreated} fragmentos agregados`);
      byId("vector-status").textContent = result.vectorEnabled ? "pgvector activo" : "Fallback local";
      byId("vector-status").classList.toggle("muted", !result.vectorEnabled);
      form.reset();
    }
  });

  byId("paper-search-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const query = new FormData(event.currentTarget).get("query");
    const results = await apiJson("/api/v1/biblioteca/search", {
      method: "POST",
      body: JSON.stringify({ query, limit: 5 }),
    });
    renderPaperResults(results || []);
  });
}

async function loadVectorHealth() {
  const health = await apiJson("/api/v1/biblioteca/health");
  if (!health) return;
  byId("vector-status").textContent = health.vectorEnabled ? "pgvector activo" : "Fallback local";
  byId("vector-status").classList.toggle("muted", !health.vectorEnabled);
}

async function loadAll() {
  renderStatus("Sincronizando");
  await Promise.all([loadLegajos(), loadAlertas()]);
  renderAll("Actualizado");
}

async function loadLegajos() {
  const data = await apiJson("/api/v1/legajos");
  state.legajos = Array.isArray(data) && data.length ? data : demoLegajos;
}

async function loadAlertas() {
  const estado = byId("alert-filter").value || "PENDIENTE";
  const data = await apiJson(`/api/v1/alertas?estado=${encodeURIComponent(estado)}`);
  if (Array.isArray(data)) {
    state.alertas = data.length ? data : demoAlertas.filter((alerta) => alerta.estado === estado);
  } else {
    state.alertas = demoAlertas.filter((alerta) => alerta.estado === estado);
  }
  renderAll("Actualizado");
}

async function apiJson(path, options = {}) {
  try {
    const response = await fetch(path, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        Authorization: state.token,
        ...(options.headers || {}),
      },
    });
    if (!response.ok) {
      throw new Error(`${response.status} ${response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.warn(`API fallback for ${path}:`, error.message);
    renderStatus("Modo demo");
    return null;
  }
}

function renderAll(statusText) {
  renderMetrics();
  renderPriorityList();
  renderLocalities();
  renderCases();
  renderAlerts();
  if (statusText) {
    renderStatus(statusText);
  }
}

function renderMetrics() {
  const pending = state.alertas.filter((alerta) => alerta.estado === "PENDIENTE").length;
  const maxScore = state.alertas.reduce((max, alerta) => Math.max(max, Number(alerta.scoreRiesgo || 0)), 0);
  const localities = new Set(state.legajos.map((legajo) => legajo.localidadPartido).filter(Boolean));
  byId("metric-cases").textContent = state.legajos.length;
  byId("metric-alerts").textContent = pending;
  byId("metric-score").textContent = maxScore.toFixed(2);
  byId("metric-localities").textContent = localities.size;
}

function renderPriorityList() {
  const list = byId("priority-list");
  const items = [...state.alertas].sort((a, b) => Number(b.scoreRiesgo || 0) - Number(a.scoreRiesgo || 0)).slice(0, 5);
  list.innerHTML = items.length ? items.map(renderAlertRow).join("") : emptyMarkup("Sin alertas prioritarias");
  bindAlertActions(list);
}

function renderLocalities() {
  const counts = state.legajos.reduce((map, legajo) => {
    const key = legajo.localidadPartido || "Sin localidad";
    map.set(key, (map.get(key) || 0) + 1);
    return map;
  }, new Map());
  const max = Math.max(...counts.values(), 1);
  byId("locality-list").innerHTML = [...counts.entries()]
    .sort((a, b) => b[1] - a[1])
    .map(([name, count]) => `
      <div class="locality-row">
        <span>${escapeHtml(name)}</span>
        <strong>${count}</strong>
        <div class="locality-track" style="--locality-width:${Math.round((count / max) * 100)}%"><span></span></div>
      </div>
    `).join("") || emptyMarkup("Sin legajos cargados");
}

function renderCases() {
  const list = byId("case-list");
  const legajos = filteredLegajos();
  list.innerHTML = legajos.length ? legajos.map((legajo) => `
    <button class="case-row ${legajo.idNnya === state.selectedCaseId ? "active" : ""}" type="button" data-case="${escapeHtml(legajo.idNnya)}">
      <span class="row-main">
        <span>${escapeHtml(legajo.idNnya)}</span>
        <span>${Number(legajo.edad || 0)} anos</span>
      </span>
      <span class="row-meta">${escapeHtml(legajo.localidadPartido || "")} · ${escapeHtml(legajo.sexo || "")} · Alta ${formatDate(legajo.fechaAltaLegajo)}</span>
    </button>
  `).join("") : emptyMarkup("Sin legajos para mostrar");
  list.querySelectorAll("[data-case]").forEach((button) => {
    button.addEventListener("click", () => selectCase(button.dataset.case));
  });
}

function renderAlerts() {
  const list = byId("alert-list");
  list.innerHTML = state.alertas.length ? state.alertas.map(renderAlertRow).join("") : emptyMarkup("Sin alertas en este estado");
  bindAlertActions(list);
}

function renderAlertRow(alerta) {
  const score = Number(alerta.scoreRiesgo || 0);
  const severity = score >= 0.8 ? "severity-high" : score >= 0.65 ? "severity-mid" : "";
  return `
    <article class="alert-row">
      <div class="row-main">
        <span>${escapeHtml(alerta.idNnya || "")}</span>
        <span class="${severity}">${score.toFixed(2)}</span>
      </div>
      <div class="score-bar" style="--score-width:${Math.round(score * 100)}%"><span></span></div>
      <span class="row-meta">${escapeHtml(alerta.explicacion || "Sin explicacion")} · ${formatDateTime(alerta.fechaCreacion)}</span>
      <div class="pill-row">
        ${["EN_REVISION", "CONFIRMADA", "DESCARTADA"].map((estado) => `
          <button class="ghost-button" type="button" data-alert="${alerta.id}" data-state="${estado}">${labelEstado(estado)}</button>
        `).join("")}
      </div>
    </article>
  `;
}

function bindAlertActions(root) {
  root.querySelectorAll("[data-alert][data-state]").forEach((button) => {
    button.addEventListener("click", async () => {
      await apiJson(`/api/v1/alertas/${button.dataset.alert}/estado?estado=${button.dataset.state}`, { method: "PATCH" });
      state.alertas = state.alertas.filter((alerta) => String(alerta.id) !== String(button.dataset.alert));
      renderAll("Alerta actualizada");
    });
  });
}

function selectCase(idNnya) {
  const legajo = state.legajos.find((item) => item.idNnya === idNnya);
  if (!legajo) return;
  state.selectedCaseId = idNnya;
  const form = byId("case-form");
  form.elements.idNnya.value = legajo.idNnya || "";
  form.elements.edad.value = legajo.edad || "";
  form.elements.sexo.value = legajo.sexo || "X";
  form.elements.localidadPartido.value = legajo.localidadPartido || "";
  form.elements.fechaAltaLegajo.value = legajo.fechaAltaLegajo || todayOffset(0);
  byId("case-editor-title").textContent = `Legajo ${legajo.idNnya}`;
  byId("selected-case-chip").textContent = legajo.localidadPartido || "Seleccionado";
  renderCases();
}

function renderEvaluation(result) {
  const score = Number(result.scoreRiesgo || 0);
  const categories = extractCategories(result.explicacion);
  byId("evaluation-result").innerHTML = `
    <div class="result-score">
      <span class="eyebrow">${escapeHtml(result.idNnya || "NNYA")}</span>
      <strong class="${score >= 0.8 ? "severity-high" : score >= 0.65 ? "severity-mid" : ""}">${score.toFixed(2)}</strong>
      <div class="score-bar" style="--score-width:${Math.round(score * 100)}%"><span></span></div>
      <span class="status-chip ${score >= 0.7 ? "" : "muted"}">${score >= 0.7 ? "Derivar" : "Monitorear"}</span>
    </div>
    <div class="result-details">
      <p>${escapeHtml(result.explicacion || "Evaluacion calculada en modo demo.")}</p>
      <div class="pill-row">${categories.map((item) => `<span class="pill">${escapeHtml(item)}</span>`).join("")}</div>
    </div>
  `;
}

function renderPaperResults(results) {
  const container = byId("paper-results");
  container.innerHTML = results.length ? results.map((item) => `
    <article class="alert-row">
      <div class="row-main">
        <span>${escapeHtml(item.title || "Paper")}</span>
        <span>${Number(item.score || 0).toFixed(2)}</span>
      </div>
      <span class="row-meta">${escapeHtml([item.authors, item.year, item.source].filter(Boolean).join(" · "))}</span>
      <p>${escapeHtml(item.content || "")}</p>
    </article>
  `).join("") : emptyMarkup("Sin resultados");
}

function collectEvaluationFeatures(form) {
  const data = new FormData(form);
  return {
    ausentismo_dias_ultimo_mes: Number(data.get("ausentismo_dias_ultimo_mes") || 0),
    cantidad_llamados_previos_linea102: Number(data.get("cantidad_llamados_previos_linea102") || 0),
    consultas_guardia_lesiones_pococlaras_ult12m: Number(data.get("consultas_guardia_lesiones_pococlaras_ult12m") || 0),
    meses_desde_ultima_intervencion: Number(data.get("meses_desde_ultima_intervencion") || 24),
    desercion_o_abandono_intermitente: data.has("desercion_o_abandono_intermitente"),
    lesiones_reportadas_por_docentes: data.has("lesiones_reportadas_por_docentes"),
    atencion_por_autolesion_o_consumo: data.has("atencion_por_autolesion_o_consumo"),
    denuncia_violencia_domestica_en_el_hogar: data.has("denuncia_violencia_domestica_en_el_hogar"),
  };
}

function localEvaluation(payload) {
  const f = payload.features;
  const score = Math.min(1, (
    Math.min(f.ausentismo_dias_ultimo_mes / 20, 1) * 0.14 +
    Math.min(f.cantidad_llamados_previos_linea102, 3) * 0.10 +
    Math.min(f.consultas_guardia_lesiones_pococlaras_ult12m / 4, 1) * 0.14 +
    (f.meses_desde_ultima_intervencion <= 6 ? 0.08 : 0) +
    (f.desercion_o_abandono_intermitente ? 0.12 : 0) +
    (f.lesiones_reportadas_por_docentes ? 0.16 : 0) +
    (f.atencion_por_autolesion_o_consumo ? 0.16 : 0) +
    (f.denuncia_violencia_domestica_en_el_hogar ? 0.16 : 0)
  ));
  const categories = inferTextCategories(payload.observacion);
  return {
    idNnya: payload.idNnya,
    scoreRiesgo: Number(score.toFixed(4)),
    estado: score >= 0.7 ? "PENDIENTE" : "DESCARTADA",
    explicacion: `Modo demo; categorias=[${categories.join(", ")}]`,
  };
}

function inferTextCategories(text) {
  const value = String(text || "").toLowerCase();
  const categories = [];
  if (value.includes("aislamiento") || value.includes("aislad")) categories.push("aislamiento");
  if (value.includes("golpe") || value.includes("lesion")) categories.push("violencia_fisica");
  if (value.includes("ausencia") || value.includes("ausent")) categories.push("ausentismo");
  return categories.length ? categories : ["sin_categoria_clara"];
}

function extractCategories(text) {
  const match = String(text || "").match(/categorias=\[([^\]]*)]/);
  if (!match) return ["scoring", "revision_humana"];
  return match[1].split(",").map((item) => item.trim()).filter(Boolean);
}

function filteredLegajos() {
  if (!state.filter) return state.legajos;
  return state.legajos.filter((legajo) => {
    return [legajo.idNnya, legajo.localidadPartido, legajo.sexo].some((value) =>
      String(value || "").toLowerCase().includes(state.filter)
    );
  });
}

function activateTab(tab) {
  document.querySelectorAll("[data-tab]").forEach((button) => button.classList.toggle("active", button.dataset.tab === tab));
  document.querySelectorAll("[data-panel]").forEach((panel) => panel.classList.toggle("active", panel.dataset.panel === tab));
}

function renderStatus(text) {
  byId("sync-status").textContent = text;
}

function upsertLocal(list, item, key) {
  const index = list.findIndex((entry) => String(entry[key]) === String(item[key]));
  if (index >= 0) list[index] = item;
  else list.unshift(item);
}

function emptyMarkup(text) {
  return `<div class="empty-state">${escapeHtml(text)}</div>`;
}

function labelEstado(estado) {
  return {
    EN_REVISION: "Revisar",
    CONFIRMADA: "Confirmar",
    DESCARTADA: "Descartar",
  }[estado] || estado;
}

function formatDate(value) {
  if (!value) return "-";
  return new Intl.DateTimeFormat("es-AR", { dateStyle: "medium" }).format(new Date(`${value}T00:00:00`));
}

function formatDateTime(value) {
  if (!value) return "-";
  return new Intl.DateTimeFormat("es-AR", { dateStyle: "short", timeStyle: "short" }).format(new Date(value));
}

function todayOffset(days) {
  const date = new Date(Date.now() + days * 24 * 60 * 60 * 1000);
  return date.toISOString().slice(0, 10);
}

function byId(id) {
  return document.getElementById(id);
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
