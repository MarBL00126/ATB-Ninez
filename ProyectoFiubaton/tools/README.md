# Paper ingestion

Carga papers psicologicos a la biblioteca vectorial de ATB-Ninez.

Uso local:

```powershell
python -m pip install pypdf
python tools/ingest_papers.py C:\ruta\a\papers --base-url http://localhost:8080
```

Uso contra Railway:

```powershell
python -m pip install pypdf
python tools/ingest_papers.py C:\ruta\a\papers --base-url https://tu-app.up.railway.app --token "Bearer demo:ADMIN:Quilmes"
```

Formatos soportados: `.pdf`, `.txt`, `.md`.
