#!/bin/bash
set -euo pipefail
repo_root="$(git rev-parse --show-toplevel)"
test_dir="$(mktemp -d "${TMPDIR:-/tmp}/obrit-registration-tests.XXXXXX")"
trap 'rm -rf "$test_dir"' EXIT

# Compile the production presentation logic on macOS. Only the unavailable iOS/KMP imports
# and platform logging/error declarations are supplied by the host-test fixture.
python3 - "$repo_root" "$test_dir" <<'PY'
from pathlib import Path
import sys
root, out = map(Path, sys.argv[1:])
paths = [
    'iosApp/iosApp/Features/Item/Models/ItemRegistrationModels.swift',
    'iosApp/iosApp/Features/Item/ItemRegistrationCatalogRepository.swift',
    'iosApp/iosApp/Features/Item/ItemRegistrationViewModel.swift',
    'iosApp/iosApp/Features/Onboarding/Models/OnboardingModels.swift',
    'iosApp/iosApp/Features/Onboarding/Models/OnboardingDefaults.swift',
    'iosApp/iosApp/Features/Onboarding/OnboardingViewModel.swift',
    'iosApp/iosApp/App/Dependencies/OnboardingCompletionStore.swift',
]
for i, path in enumerate(paths):
    source = (root / path).read_text().replace('import Shared', 'import Combine')
    (out / f'Source{i}.swift').write_text(source)
config = (root / 'iosApp/iosApp/Features/Item/Views/Registration/ItemRegistrationConfiguration.swift').read_text()
config = config[config.index('enum ItemRegistrationConfig {'):config.index('enum ItemRegistrationText {')]
protocol = (root / 'iosApp/iosApp/Features/Onboarding/OnboardingRepository.swift').read_text()
protocol = protocol[protocol.index('protocol OnboardingRepository {'):protocol.index('actor SharedOnboardingRepository')]
(out / 'PlatformFixture.swift').write_text('''import Foundation
import Combine
@MainActor protocol OnboardingNotificationSetup { func enableAndRequestPermission() async }
enum CreateItemError { struct DuplicatedName: Error {} }
enum CreateCategoryError { struct DuplicatedName: Error {} }
enum AppLog {
    static let itemRegistrationViewModel = 0
    static func enter(_ logger: Int, _ event: String, _ details: String = "") {}
    static func success(_ logger: Int, _ event: String, _ details: String = "") {}
    static func failure(_ logger: Int, _ event: String, _ error: Error, _ details: String = "") {}
}
''' + config + protocol)
PY
swiftc -swift-version 5 -module-cache-path "$test_dir/cache" \
    "$test_dir"/*.swift "$repo_root/iosApp/Tests/RegistrationParityTests.swift" \
    -o "$test_dir/registration-tests"
"$test_dir/registration-tests"
