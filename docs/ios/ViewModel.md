# iOS ViewModel Convention

iOS ViewModel은 SwiftUI 화면을 위한 presentation state를 관리한다. Android의 Orbit MVI 구조와 동일한 책임 분리를 유지하되, iOS에서는 SwiftUI와 Swift concurrency에 맞게 표현한다.

## 파일 배치

- ViewModel은 `iosApp/iosApp/<Feature>/` 아래에 `FeatureViewModel.swift`로 둔다.
- ViewModel과 가까운 state, effect 타입은 같은 파일에 둔다.
- 여러 화면에서 공유되는 UI 상태 타입만 별도 파일로 분리한다.

## 생성자와 의존성

- ViewModel은 repository나 use case 같은 shared/domain API에 의존한다.
- SwiftUI View에서 remote data source나 HTTP client를 직접 만들지 않는다.
- ViewModel은 `@MainActor final class`로 작성하고, 화면 state는 `@Published` 또는 Observation API로 노출한다.
- shared Kotlin API를 호출할 때는 Kotlin/Native export 이름, nullability, suspend bridging 방식을 확인한다.

```swift
@MainActor
final class AgentViewModel: ObservableObject {
    @Published private(set) var state: AgentViewState = .loading

    func createAgent(name: String, description: String, type: AgentType) {
        // shared/domain API 호출 후 state 또는 effect를 갱신한다.
    }
}
```

## State

- state는 `enum FeatureViewState`로 정의한다.
- 성공 상태는 associated value로 필요한 데이터를 담는다.
- 단순 상태는 `.loading`, `.loadFailed`처럼 표현한다.
- optional field 조합으로 화면 상태를 암시하지 말고 state enum으로 표현한다.

```swift
enum AgentViewState {
    case loading
    case loadFailed
    case success(agents: [Agent], sessions: [Session])
}
```

## Effect

- navigation, alert, toast/snackbar에 해당하는 일회성 이벤트는 state와 분리한다.
- effect 이름은 UI가 해야 할 일을 기준으로 작성한다.
- 구현 방식은 화면 규모에 따라 callback, `@Published var effect`, `AsyncStream` 중 하나를 선택하되, 지속 상태와 섞지 않는다.

## Async 작업

- UI event 함수는 Android action과 1:1로 읽히게 작성한다.
- async 작업은 `Task` 또는 async 함수로 실행하고, state 갱신은 main actor에서 수행한다.
- 실패는 shared/domain error를 iOS presentation error로 변환한 뒤 effect 또는 failure state로 전달한다.

## 금지 사항

- ViewModel에서 SwiftUI View를 생성하지 않는다.
- ViewModel에서 Android Compose, Android ViewModel, Android resource 개념을 참조하지 않는다.
- ViewModel에서 DTO나 remote response 타입을 직접 다루지 않는다.
- 네트워크 예외 타입을 그대로 UI state에 오래 보관하지 않는다.

