#!/bin/sh
set -eu

ROOT_DIR="$(git rev-parse --show-toplevel)"
cd "$ROOT_DIR"

PATH="/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:$PATH"
export PATH

if ! command -v swiftformat >/dev/null 2>&1; then
  echo "ios-format: swiftformat not found. Install it with: brew install swiftformat"
  exit 1
fi

swiftformat --cache ignore iosApp/iosApp
