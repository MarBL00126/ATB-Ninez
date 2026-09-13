from config.settings import get_settings


def get_collection_names() -> tuple[str, str]:
    settings = get_settings()
    return settings.observations_collection, settings.regulations_collection
