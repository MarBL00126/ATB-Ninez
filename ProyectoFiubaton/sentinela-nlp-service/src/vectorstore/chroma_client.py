from config.settings import get_settings


def create_chroma_client():
    settings = get_settings()
    settings.chroma_path.mkdir(parents=True, exist_ok=True)
    try:
        import chromadb

        return chromadb.PersistentClient(path=str(settings.chroma_path))
    except Exception:
        return None
