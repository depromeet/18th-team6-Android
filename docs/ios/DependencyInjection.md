# iOS Dependency Injection Convention

iOS는 SwiftUI composition root에서 화면과 ViewModel 의존성을 조립한다. shared DI의 공통 규칙은 `docs/ruleset/DependencyInjection.md`를 따른다.

## App 조립

- iOS 앱 진입점은 `iosApp/iosApp/iOSApp.swift`이다.
- shared Koin 초기화가 필요하면 Swift에서 호출 가능한 KMP API를 제공하고 앱 시작 시 한 번만 호출한다.
- Android `Context`나 Android 전용 Koin API를 iOS 초기화 경로에 넣지 않는다.

## ViewModel 생성

- SwiftUI View는 필요한 ViewModel을 composition boundary에서 주입받거나 `@StateObject`로 보관한다.
- repository/use case 생성은 View 내부가 아니라 factory, environment, app composition root에서 처리한다.
- 아직 iOS DI framework가 없다면 단순 factory 함수로 시작하고, 실제 필요가 생길 때만 별도 DI 도구를 도입한다.

```swift
struct AgentView: View {
    @StateObject private var viewModel: AgentViewModel

    init(viewModel: AgentViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }
}
```

## 금지 사항

- SwiftUI View에서 HTTP client나 remote data source 구현체를 직접 생성하지 않는다.
- iOS composition code에서 Android module이나 Android context를 참조하지 않는다.
- DI 편의를 위해 shared 구현체 visibility를 불필요하게 넓히지 않는다.

