import argparse
import json
from pathlib import Path
from urllib import request


SUPPORTED_EXTENSIONS = {".pdf", ".txt", ".md"}


def extract_text(path: Path) -> str:
    if path.suffix.lower() == ".pdf":
        try:
            from pypdf import PdfReader
        except ImportError as exc:
            raise RuntimeError("Install pypdf to ingest PDFs: python -m pip install pypdf") from exc
        reader = PdfReader(str(path))
        return "\n".join(page.extract_text() or "" for page in reader.pages)
    return path.read_text(encoding="utf-8")


def post_json(base_url: str, token: str, payload: dict) -> dict:
    data = json.dumps(payload).encode("utf-8")
    req = request.Request(
        f"{base_url.rstrip('/')}/api/v1/biblioteca/papers",
        data=data,
        headers={
            "Content-Type": "application/json",
            "Authorization": token,
        },
        method="POST",
    )
    with request.urlopen(req, timeout=60) as response:
        return json.loads(response.read().decode("utf-8"))


def iter_documents(input_dir: Path):
    for path in sorted(input_dir.rglob("*")):
        if path.is_file() and path.suffix.lower() in SUPPORTED_EXTENSIONS:
            yield path


def main():
    parser = argparse.ArgumentParser(description="Ingest psychology papers into ATB-Ninez vector library.")
    parser.add_argument("input_dir", help="Folder containing PDF, TXT, or MD papers.")
    parser.add_argument("--base-url", default="http://localhost:8080", help="ATB-Ninez app URL.")
    parser.add_argument("--token", default="Bearer demo:ADMIN:Quilmes", help="Authorization token.")
    parser.add_argument("--authors", default="", help="Default authors when not encoded elsewhere.")
    parser.add_argument("--source", default="", help="Default source label.")
    parser.add_argument("--year", type=int, default=None, help="Default publication year.")
    args = parser.parse_args()

    input_dir = Path(args.input_dir)
    if not input_dir.exists():
        raise SystemExit(f"Input folder does not exist: {input_dir}")

    count = 0
    for path in iter_documents(input_dir):
        text = extract_text(path)
        if not text.strip():
            print(f"SKIP empty: {path}")
            continue
        payload = {
            "title": path.stem.replace("_", " ").replace("-", " ").strip(),
            "authors": args.authors,
            "year": args.year,
            "source": args.source or str(path),
            "text": text,
        }
        result = post_json(args.base_url, args.token, payload)
        count += 1
        print(f"OK {path.name}: {result['chunksCreated']} chunks")
    print(f"Done. Ingested {count} documents.")


if __name__ == "__main__":
    main()
