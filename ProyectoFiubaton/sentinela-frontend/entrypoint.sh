#!/bin/sh
set -eu

cat > /usr/share/nginx/html/config.js <<EOF
window.ATB_CONFIG = {
  API_BASE_URL: "${API_BASE_URL:-}"
};
EOF

exec "$@"
