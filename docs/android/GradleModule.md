# Android Gradle Module Convention

Android Gradle 설정은 `build-logic/`의 convention plugin과 `gradle/libs.versions.toml`을 기준으로 관리한다.

## Convention Plugin

- Android app module은 `alias(libs.plugins.obrit.android.application)`을 우선 사용한다.
- Android Compose library module은 `alias(libs.plugins.obrit.android.library.compose)`를 우선 사용한다.
- Android non-Compose library module은 `alias(libs.plugins.obrit.android.library)`를 우선 사용한다.
- 예외적으로 직접 plugin을 적용해야 하면 이유를 문서나 PR 설명에 남긴다.

## Dependency 선언

- Compose dependencies는 Compose BOM을 함께 사용한다.
- Android module dependency는 `implementation(projects.android.core.ui)` 같은 type-safe accessor로 선언한다.
- shared module dependency는 `implementation(projects.shared.data)` 같은 accessor로 선언한다.
- 불필요하게 app module에 하위 구현 dependency를 몰아넣지 않는다.

## Build Logic

- Android SDK, Java, Kotlin target 설정은 가능한 `build-logic`에서 관리한다.
- convention plugin을 사용하는 Android module의 namespace는 Gradle path 기반으로 생성된다.
- compile SDK, min SDK, app version은 version catalog 값을 따른다.

## Verification

- Android library 변경은 관련 `compileDebugKotlin`을 확인한다.
- app wiring 변경은 `:android:app:compileDebugKotlin` 또는 `:android:app:assembleDebug`를 확인한다.

```powershell
.\gradlew.bat :android:core:ui:compileDebugKotlin
.\gradlew.bat :android:app:compileDebugKotlin
.\gradlew.bat :android:app:assembleDebug
```

