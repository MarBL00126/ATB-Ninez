from pathlib import Path

import joblib
import pandas as pd
from sklearn.compose import ColumnTransformer
from sklearn.impute import SimpleImputer
from sklearn.metrics import roc_auc_score
from sklearn.model_selection import cross_val_score, train_test_split
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from xgboost import XGBClassifier

from training.dataset import load_dataset
from training.features import prepare_training_frame


def train(dataset_path: str, output_dir: str = "models", target_column: str = "alerta_binaria") -> dict:
    output_path = Path(output_dir)
    output_path.mkdir(parents=True, exist_ok=True)

    frame = prepare_training_frame(load_dataset(dataset_path))
    if target_column not in frame:
        raise ValueError(f"Target column '{target_column}' not found")

    x = frame.drop(columns=[target_column, "id_nnya"], errors="ignore")
    y = frame[target_column]
    numeric_columns = x.select_dtypes(include=["number", "bool"]).columns.tolist()
    categorical_columns = [column for column in x.columns if column not in numeric_columns]

    preprocessor = ColumnTransformer(
        transformers=[
            ("numeric", Pipeline([("imputer", SimpleImputer()), ("scaler", StandardScaler())]), numeric_columns),
            ("categorical", Pipeline([("imputer", SimpleImputer(strategy="most_frequent")), ("onehot", OneHotEncoder(handle_unknown="ignore"))]), categorical_columns),
        ]
    )
    model = XGBClassifier(
        n_estimators=120,
        max_depth=3,
        learning_rate=0.08,
        eval_metric="logloss",
        random_state=42,
    )
    pipeline = Pipeline([("preprocessor", preprocessor), ("model", model)])
    x_train, x_test, y_train, y_test = train_test_split(x, y, test_size=0.2, random_state=42, stratify=y)
    pipeline.fit(x_train, y_train)

    probabilities = pipeline.predict_proba(x_test)[:, 1]
    metrics = {
        "roc_auc": float(roc_auc_score(y_test, probabilities)),
        "cv_roc_auc": float(cross_val_score(pipeline, x, y, cv=3, scoring="roc_auc").mean()),
    }
    joblib.dump(pipeline.named_steps["preprocessor"], output_path / "preprocessor.joblib")
    joblib.dump(pipeline, output_path / "risk_pipeline.joblib")
    pipeline.named_steps["model"].save_model(output_path / "xgboost_risk_v1.json")
    return metrics


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument("dataset_path")
    parser.add_argument("--output-dir", default="models")
    parser.add_argument("--target-column", default="alerta_binaria")
    args = parser.parse_args()
    print(train(args.dataset_path, args.output_dir, args.target_column))
