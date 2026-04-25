# iOS SwiftUI Convention

iOS UI는 SwiftUI 기반으로 작성하며 `iosApp/iosApp/` 아래에 둔다. SwiftUI 코드는 Android Compose나 Android ViewModel에 의존하지 않는다.

## 파일 배치

- 화면 단위 코드는 `iosApp/iosApp/<Feature>/` 아래에 둔다.
- 진입점 View는 `FeatureView.swift` 형식을 사용한다.
- 상태별 콘텐츠가 커지면 `FeatureContentView.swift`, `FeatureLoadingView.swift`, `FeatureFailureView.swift`, `FeatureSuccessView.swift`처럼 분리한다.
- 여러 화면에서 쓰는 SwiftUI component는 iOS 전용 shared UI 폴더를 만든 뒤 재사용한다.

## View 작성

- `FeatureView`는 ViewModel 보관, state 관찰, effect 처리, action 연결을 담당한다.
- 실제 화면 렌더링은 `FeatureContentView` 또는 private `@ViewBuilder`로 분리한다.
- repository, remote data source, Ktor client를 SwiftUI View에서 직접 생성하지 않는다.
- shared Kotlin API는 ViewModel이나 composition boundary를 통해 호출한다.

```swift
struct AgentView: View {
    @StateObject private var viewModel: AgentViewModel

    var body: some View {
        AgentContentView(
            state: viewModel.state,
            action: AgentViewAction(
                onCreateAgent: viewModel.createAgent,
                onDeleteAgent: viewModel.deleteAgent,
                onSelectAgent: viewModel.selectAgent
            )
        )
    }
}
```

## Action 객체

- 화면 이벤트는 `FeatureViewAction` struct로 묶는다.
- action은 UI 이벤트 이름으로 작성한다.
- action 안에서 비즈니스 로직을 실행하지 않는다.
- action 타입은 화면에 필요한 최소 인자만 받는다.

## State 렌더링

- state는 ViewModel의 `FeatureViewState` enum을 기준으로 분기한다.
- loading, failure, success UI는 명확히 분리한다.
- localization, alert message, navigation 표현은 iOS UI 계층에서 처리한다.
- domain model 자체를 보여줘도 되지만 UI 전용 포맷이 복잡해지면 별도 ViewData를 고려한다.

## Preview

- preview는 가능한 content view 대상으로 작성한다.
- preview에서 실제 shared repository, network, Koin 초기화에 의존하지 않는다.
- sample state와 fake action을 사용한다.

