# Android Presentation Rules

이 디렉터리는 Android presentation layer 전용 규칙을 정리한다. Android와 iOS가 함께 쓰는 domain/data/network 규칙은 `docs/ruleset/`에 둔다.

## 문서 목록

- [Compose.md](Compose.md) - Compose 화면, Content, Action, state rendering 규칙
- [Navigation.md](Navigation.md) - Navigation3 기반 root/feature graph, route key, back stack 규칙
- [ViewModel.md](ViewModel.md) - Orbit MVI 기반 ViewModel, state, side effect 규칙
- [DependencyInjection.md](DependencyInjection.md) - Android app과 화면 모듈의 Koin 조립 규칙
- [GradleModule.md](GradleModule.md) - Android app/library Gradle module 규칙

## 구조 원칙

- 앱 진입 navigation은 `OBRitNavigation`에서 시작하고, feature 내부 흐름은 `FeatureNavigation`으로 분리한다.
- 화면은 `Screen -> ScreenContent -> 상태별 Content` 흐름으로 분리한다.
- 화면 이벤트는 `ScreenAction`으로 묶고, 실제 처리는 ViewModel에 위임한다.
- ViewModel은 repository에 의존하고, UI는 repository나 remote data source를 직접 호출하지 않는다.
- 일회성 이벤트는 side effect로 전달한다.
- Android resource 변환과 snackbar/dialog/navigation 처리는 Android UI 계층에서 수행한다.
