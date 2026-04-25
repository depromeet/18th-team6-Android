# iOS Presentation Rules

이 디렉터리는 iOS presentation layer 전용 규칙을 정리한다. 현재 iOS 화면 구현은 작지만, Android presentation layer의 `Screen/Content/Action/ViewModel` 분리 원칙을 SwiftUI에 맞게 대응한다.

## 문서 목록

- [SwiftUI.md](SwiftUI.md) - SwiftUI 화면, Content, Action, state rendering 규칙
- [ViewModel.md](ViewModel.md) - SwiftUI ViewModel, state, effect 규칙
- [DependencyInjection.md](DependencyInjection.md) - iOS composition root와 shared dependency 연결 규칙

## Android 구조와의 대응

- Android `Screen`은 iOS `FeatureView`에 대응한다.
- Android `ScreenContent`는 iOS `FeatureContentView` 또는 private body builder에 대응한다.
- Android `ScreenAction`은 iOS action closure struct에 대응한다.
- Android `UiState`는 iOS `FeatureViewState` enum에 대응한다.
- Android side effect는 iOS `FeatureViewEffect` 또는 ViewModel callback/async stream에 대응한다.

