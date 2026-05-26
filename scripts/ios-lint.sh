#!/bin/sh
set -eu

ROOT_DIR="$(git rev-parse --show-toplevel)"
cd "$ROOT_DIR"

PATH="/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:$PATH"
export PATH

if ! command -v swiftformat >/dev/null 2>&1; then
  echo "ios-lint: swiftformat not found. Install it with: brew install swiftformat"
  exit 1
fi

if ! command -v swiftlint >/dev/null 2>&1; then
  echo "ios-lint: swiftlint not found. Install it with: brew install swiftlint"
  exit 1
fi

scripts/ios-style-check.sh
swiftformat --cache ignore --lint iosApp/iosApp
swiftlint lint --quiet --no-cache --config .swiftlint.yml
