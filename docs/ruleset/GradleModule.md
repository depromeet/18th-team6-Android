# Gradle Module Convention

Gradle 설정은 루트 `settings.gradle.kts`, `gradle/libs.versions.toml`, `build-logic/`의 convention plugin을 기준으로 관리한다. 이 문서는 shared/KMP 공통 규칙만 다루며, Android app/library 규칙은 `docs/android/GradleModule.md`를 따른다.

## Module 등록

- 새 Gradle module은 반드시 `settings.gradle.kts`에 include한다.
- include되지 않은 디렉터리는 빌드 대상이 아니다.
- type-safe project accessor인 `projects.*`를 사용한다.
- dependency version은 `gradle/libs.versions.toml`에 추가한다.

## Convention Plugin

- KMP shared module은 `alias(libs.plugins.obrit.kotlin.multiplatform)`을 우선 사용한다.
- 예외적으로 직접 plugin을 적용해야 하면 이유를 문서나 PR 설명에 남긴다.

## Dependency 선언

- shared module의 Koin과 Ktor dependencies는 각각 BOM을 사용한다.
- module dependency는 `implementation(projects.shared.data)` 같은 accessor로 선언한다.
- API로 노출되어야 하는 domain dependency만 `api`를 사용한다.
- 불필요하게 상위 module에 하위 구현 dependency를 몰아넣지 않는다.

## Build Logic

- 공통 Java/Kotlin target 설정은 `build-logic`에서 관리한다.
- version 값은 `gradle/libs.versions.toml`에 둔다.
- lint, detekt, ktlint 설정은 루트 plugin과 config 파일을 따른다.

## Verification

- KMP 변경은 먼저 관련 `compileKotlinMetadata`를 확인한다.

```powershell
.\gradlew.bat :shared:compileKotlinMetadata
.\gradlew.bat :shared:network:compileKotlinMetadata
.\gradlew.bat :shared:data:compileKotlinMetadata
```
