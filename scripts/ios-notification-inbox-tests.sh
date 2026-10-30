#!/bin/bash
set -euo pipefail

repo_root="$(git rev-parse --show-toplevel)"
test_dir="$(mktemp -d "${TMPDIR:-/tmp}/obrit-notification-tests.XXXXXX")"
trap 'rm -rf "$test_dir"' EXIT

swiftc -swift-version 6 -module-cache-path "$test_dir/cache" \
    "$repo_root/iosApp/iosApp/Features/NotificationInbox/NotificationInboxModels.swift" \
    "$repo_root/iosApp/iosApp/Features/NotificationInbox/NotificationInboxRepository.swift" \
    "$repo_root/iosApp/iosApp/Features/NotificationInbox/NotificationInboxViewModel.swift" \
    "$repo_root/iosApp/iosApp/Features/NotificationInbox/NotificationOpenCenter.swift" \
    "$repo_root/iosApp/Tests/NotificationInboxTests.swift" \
    -o "$test_dir/notification-inbox-tests"
"$test_dir/notification-inbox-tests"
