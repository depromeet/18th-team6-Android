# iOS AGENTS.md

이 문서는 iOS 구현 시 AI 코딩 에이전트가 따라야 할 전용 지침이다. 루트 `AGENTS.md`에는 공통 작업 규칙만 두고, iOS 세부 구현 규칙은 이 파일을 기준으로 한다.

## iOS 프로젝트 구조

- SwiftUI host code는 `iosApp/iosApp/` 아래에 둔다.
- iOS 앱 진입점은 `iosApp/iosApp/iOSApp.swift`이다.
- 현재 기본 화면 예시는 `iosApp/iosApp/ContentView.swift`에 있다.
- Xcode project 설정은 `iosApp/iosApp.xcodeproj/` 아래에서 관리한다.
- KMP shared framework는 iOS에서 `Shared`로 import한다.

## Shared Framework

- `shared/build.gradle.kts`에서 Kotlin/Native framework의 `baseName`은 `Shared`로 설정되어 있다.
- iOS Swift code는 `import Shared`를 사용하므로 framework 이름을 바꾸면 Swift import와 Xcode 설정을 함께 맞춘다.
- shared framework는 현재 static framework로 설정되어 있다. dynamic framework로 바꾸는 작업은 iOS build/link 영향까지 확인한다.

## KMP Source Set

- cross-platform domain, repository contract, model, data logic은 가능한 `commonMain`에 둔다.
- iOS 전용 Kotlin 구현은 해당 모듈의 `src/iosMain/kotlin` 아래에 둔다.
- Android 전용 API를 `commonMain`이나 `iosMain`에서 참조하지 않는다.
- `expect`/`actual`을 추가할 때는 common 선언과 iOS actual 구현을 함께 작성한다.

## Network

- `shared:network`의 iOS HTTP engine actual 구현은 `shared/network/src/iosMain/.../HttpClientFactory.ios.kt`에 있다.
- iOS에서는 Ktor Darwin engine을 사용한다.
- HTTP client 공통 설정, JSON 설정, error 변환은 common code에 둔다.
- iOS만 필요한 network 설정이 생기면 `iosMain`에 한정하고 Android 동작을 바꾸지 않는다.

## SwiftUI 구현 규칙

- iOS presentation layer 세부 규칙은 `docs/ios/README.md`를 따른다.
- SwiftUI 화면은 `iosApp/iosApp/` 아래에 둔다.
- SwiftUI code는 shared domain API를 호출하되, Android Compose code나 Android ViewModel에 의존하지 않는다.
- shared Kotlin API를 Swift에서 사용할 때는 Kotlin/Native export 이름과 nullability를 확인한다.
- sample UI를 수정할 때도 `ContentView_Previews`가 깨지지 않도록 유지한다.
- 파일을 나누기 전에는 분리가 과한 추상화가 아닌지 먼저 판단한다.
  - 단순 wrapper, 한 곳에서만 쓰는 얇은 pass-through view, 이름만 바꾼 content view는 별도 파일로 분리하지 않는다.
  - 화면 진입점과 content view를 분리할 때는 preview/test 재사용, 상태별 렌더링 독립성, 파일 크기 감소 같은 명확한 이점이 있어야 한다.
  - 분리 후에도 각 파일이 독립적인 책임을 갖지 못하거나 호출 단계만 늘어난다면 기존 파일 안의 private helper/view로 유지한다.

## KMP 비즈니스 로직 연결 경계

- SwiftUI View는 Swift ViewModel에만 의존하고, Android Compose code나 Android ViewModel을 참조하지 않는다.
- Swift ViewModel은 KMP shared repository 또는 Swift-facing shared facade/provider를 통해 비즈니스 로직을 호출한다.
- KMP shared code는 API 호출, repository contract와 구현, domain model, typed error, 공통 business rule을 담당한다.
- iOS Swift code는 SwiftUI 화면 state, 버튼/시트/네비게이션 상태, 화면 문구, iOS 전용 view data를 담당한다.
- KMP DTO, remote data source, Ktor client는 SwiftUI presentation layer에 직접 노출하지 않는다.
- facade/provider를 추가할 때는 repository 조립, Koin 접근, Kotlin `Result`/error 변환처럼 Swift interop 부담을 줄이는 목적에 한정한다. 화면 문구나 SwiftUI 전용 view data를 Kotlin에서 만들기 위한 facade는 만들지 않는다.

## Git 커밋 규칙

- iOS 작업 커밋의 제목과 본문은 한글로 작성한다.
- 커밋 타입은 루트 `AGENTS.md`와 `docs/ruleset/Git.md`의 Conventional Commits 형식을 따르되, 요약과 설명 문장은 한글로 작성한다.
- 예: `feat: 홈 리스트 탭 추가`, `fix: GNB 탭 전환 시 네비게이션 스택 초기화`

## 검증

- shared Kotlin 변경은 먼저 관련 Gradle metadata compile을 확인한다.

```powershell
.\gradlew.bat :shared:compileKotlinMetadata
.\gradlew.bat :shared:network:compileKotlinMetadata
.\gradlew.bat :shared:data:compileKotlinMetadata
```

- iOS source set이나 framework 설정을 바꾼 경우 Xcode build 또는 KMP iOS framework build로 확인한다.
- 이 Windows workspace에서 Xcode 검증을 실행할 수 없으면 실행하지 못한 이유를 보고한다.
