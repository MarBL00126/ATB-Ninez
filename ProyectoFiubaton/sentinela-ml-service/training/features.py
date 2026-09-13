from src.pipelines.feature_engineering import build_feature_vector


def prepare_training_frame(frame):
    engineered = [build_feature_vector(row.dropna().to_dict()) for _, row in frame.iterrows()]
    return frame.assign(**{key: [row[key] for row in engineered] for key in engineered[0]}) if engineered else frame
