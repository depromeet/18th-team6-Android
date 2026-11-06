#!/bin/bash
set -euo pipefail

repo_root="$(git rev-parse --show-toplevel)"
test_dir="$(mktemp -d "${TMPDIR:-/tmp}/obrit-widget-tests.XXXXXX")"
trap 'rm -rf "$test_dir"' EXIT

swiftc -swift-version 6 -module-cache-path "$test_dir/cache" \
    "$repo_root/iosApp/WidgetShared/StatusWidgetState.swift" \
    "$repo_root/iosApp/Tests/StatusWidgetStateTests.swift" \
    -o "$test_dir/widget-state-tests"
"$test_dir/widget-state-tests"


swiftc -swift-version 6 -module-cache-path "$test_dir/cache" \
    "$repo_root/iosApp/WidgetShared/StatusWidgetState.swift" \
    "$repo_root/iosApp/WidgetShared/StatusWidgetCache.swift" \
    "$repo_root/iosApp/WidgetShared/StatusWidgetDataSource.swift" \
    "$repo_root/iosApp/Tests/StatusWidgetDataSourceTests.swift" \
    -o "$test_dir/widget-data-source-tests"
"$test_dir/widget-data-source-tests"
