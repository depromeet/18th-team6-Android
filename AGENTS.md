# AGENTS.md

이 파일은 이 저장소에서 AI 코딩 에이전트가 따라야 할 기본 작업 가이드다. 현재 프로젝트 구조와 Gradle 설정을 기준으로 작성되었다.

## 프로젝트 개요

- OBRit은 Android Compose 앱과 iOS SwiftUI 호스트를 가진 Kotlin Multiplatform 프로젝트다.
- 현재 활성 Gradle 모듈은 `settings.gradle.kts`에 선언되어 있다.
  - `:android:app` - Android 애플리케이션 진입점, Compose 설정, Android Koin 부트스트랩
  - `:android:feature:agent` - Koin과 Orbit MVI를 사용하는 Android Compose agent 기능
  - `:android:core:ui` - Android feature 모듈에서 공유하는 UI/ViewModel 유틸리티
  - `:shared` - KMP shared 모듈, iOS framework base name `Shared`, shared Koin 모듈 집계
  - `:shared:model` - 공통 domain model과 typed error 클래스
  - `:shared:network` - 공통 Ktor client, serialization DTO, remote data source, Android/iOS HTTP engine
  - `:shared:data` - network 데이터를 model API로 매핑하는 repository 인터페이스와 구현체
  - `:shared:design-system` - 공통 design system token
- `android/core/design-system` 디렉터리는 존재하지만 현재 `settings.gradle.kts`에 include 되어 있지 않다. 의도적으로 연결하기 전까지는 빌드에 참여한다고 가정하지 않는다.
- `build-logic/`에는 Gradle convention plugin이 있다. 임의로 모듈 설정을 늘리기보다 기존 plugin과 version catalog를 우선 사용한다.
- `docs/`, `rules/`, `skills/`, `hooks/`는 문서 또는 workflow 자산이며 application module이 아니다.

## 작업 규칙

- 수정 전후로 `git status --short`를 확인한다.
- 동작을 바꾸기 전에 주변 source와 build file을 먼저 읽는다.
- 변경은 요청과 직접 관련된 범위로 작게 유지하고, 기존 module boundary를 따른다.
- 관련 없는 formatting, generated file, TODO, comment를 고치지 않는다.
- 사용자가 명시적으로 요청하지 않는 한 사용자 변경분을 되돌리지 않는다.
- 별도 지시가 없으면 이 workspace에서는 Windows/PowerShell 명령을 사용한다.
- secret과 로컬 머신 경로를 commit 대상 파일에 넣지 않는다. `local.properties`는 local-only 파일이다.

## 모듈 경계

- cross-platform domain, repository contract, serialization과 독립적인 model, 재사용 가능한 data logic은 `shared/*` 아래에 둔다.
- platform-specific KMP 구현은 `androidMain`, `iosMain` 같은 알맞은 source set에 둔다.
- Android UI, Android ViewModel, Android DI entry point, Compose screen은 `android/*` 아래에 둔다.
- SwiftUI host code는 `iosApp/` 아래에 둔다.
- Gradle 모듈을 추가할 때는 `settings.gradle.kts`에 include하고, 가능하면 기존 convention plugin을 사용하며, `projects.*` type-safe project accessor를 우선한다.
- convention plugin을 사용하는 Android module의 namespace는 build logic에서 Gradle path를 기준으로 생성된다.

## 기존 패턴

- Dependency injection은 Koin module을 사용한다.
  - shared module은 `shared/src/commonMain/.../di/SharedKoin.kt`에서 집계한다.
  - Android feature module은 Android `Application`에서 추가한다.
- Android feature state 관리는 Orbit MVI를 사용하며 `BaseContainerHost`, `container`, `intent`, `reduce`, side effect 패턴을 따른다.
- Network code는 `shared:network`의 Ktor를 사용하고, Android/iOS HTTP engine 선택은 `expect`/`actual`로 분리한다.
- Remote error는 `RemoteError`, `RootError`, `runCatchingWith`를 통해 변환한다. 주변 패턴이 바뀌지 않는 한 repository API는 `Result<T>`를 반환한다.
- Network DTO는 `shared:network`에 두고, domain model은 `shared:model`에 둔다. DTO를 domain model로 바꾸는 mapping function을 사용한다.
- Compose code는 현재 Material/Compose dependency 구성과 local screen/action/state naming을 따른다.

## 빌드와 검증

수정한 파일을 포함하는 가장 작은 Gradle task를 우선 사용한다.

```powershell
.\gradlew.bat :shared:compileKotlinMetadata
.\gradlew.bat :shared:network:compileKotlinMetadata
.\gradlew.bat :shared:data:compileKotlinMetadata
.\gradlew.bat :android:core:ui:compileDebugKotlin
.\gradlew.bat :android:feature:agent:compileDebugKotlin
.\gradlew.bat :android:app:compileDebugKotlin
```

Android 전체 검증이 필요하면 다음 명령을 사용한다.

```powershell
.\gradlew.bat :android:app:assembleDebug
```

lint/style 확인이 관련된 경우 다음 명령을 사용한다.

```powershell
.\gradlew.bat detekt ktlintCheck
```

문서-only 변경이나 agent guide 변경은 보통 Gradle build가 필요하지 않다. 검증을 생략했다면 이유를 함께 남긴다.

## 코드 스타일

- Kotlin은 root build에 설정된 ktlint와 detekt 기반의 official style을 따른다.
- `.editorconfig`에 정의된 대로 UTF-8, LF line ending, 4-space indentation, final newline을 사용한다.
- Compose preview와 composable은 기존 naming rule을 따른다. `@Composable`에 대해서는 function naming이 완화되어 있다.
- 추측성 abstraction보다 명확한 이름과 작은 함수를 우선한다.
- comment는 동작이 바로 이해되지 않는 경우에만 추가한다.

## 현재 프로젝트 참고 사항

- 일부 agent feature screen과 network configuration에는 TODO placeholder가 남아 있다. 해당 작업이 요청된 경우가 아니면 기존 미완성 지점으로 취급하고 건드리지 않는다.
- `NetworkConfiguration.DEFAULT_NETWORK_CONFIGURATION`은 현재 placeholder base URL을 사용한다. private endpoint나 secret을 hardcode하지 않는다.
- iOS app은 shared framework를 `Shared`로 import한다. framework base name을 바꿀 경우 iOS 쪽과 함께 맞춘다.

## Git 위생

- 사용자 요청과 관련된 파일만 stage한다.
- 사용자가 명시적으로 요청하지 않는 한 branch, commit, tag, pull request를 만들지 않는다.
- 수정 후에는 변경한 파일과 수행한 검증을 보고한다.
