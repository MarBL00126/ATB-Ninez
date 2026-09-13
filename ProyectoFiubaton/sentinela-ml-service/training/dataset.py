from pathlib import Path

import pandas as pd


REQUIRED_COLUMNS = {"id_nnya"}


def load_dataset(path: str | Path) -> pd.DataFrame:
    """Load a tabular dump exported from PostgreSQL or a CSV fixture."""
    dataset_path = Path(path)
    if dataset_path.suffix.lower() == ".parquet":
        frame = pd.read_parquet(dataset_path)
    else:
        frame = pd.read_csv(dataset_path)
    missing = REQUIRED_COLUMNS - set(frame.columns)
    if missing:
        raise ValueError(f"Missing required columns: {sorted(missing)}")
    return frame
