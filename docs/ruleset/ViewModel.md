# ViewModel Convention

Android ViewModel은 Orbit MVI 패턴을 따른다. 현재 공통 기반 클래스는 `android:core:ui`의 `BaseContainerHost`이다.

## 파일 배치

- feature ViewModel은 feature 모듈의 `viewmodel` 패키지에 둔다.
- ViewModel과 가까운 state, side effect 타입은 같은 파일에 둔다.
- 여러 화면에서 공유되는 UI 상태 타입만 별도 파일로 분리한다.

## 생성자와 DI

- ViewModel 생성자는 Koin에서 주입할 repository만 받는다.
- 생성자는 외부에서 직접 만들 필요가 없으면 `internal constructor`를 사용한다.
- Android `Context`, `Activity`, `Composable` 타입을 ViewModel에 넣지 않는다.

```kotlin
class AgentViewModel internal constructor(
    private val agentRepository: AgentRepository,
    private val agentSessionRepository: AgentSessionRepository,
) : BaseContainerHost<AgentUiState, AgentSideEffect>() {
    override val container = container<AgentUiState, AgentSideEffect>(AgentUiState.Loading)
}
```

## State

- state는 `sealed interface FeatureUiState`로 정의한다.
- 성공 상태는 `data class Success(...)`로 작성한다.
- 단순 상태는 `data object Loading`, `data object LoadFailed`처럼 작성한다.
- Compose에서 안정성이 중요한 성공 상태에는 필요할 때 `@Immutable`을 붙인다.
- nullable field로 화면 상태를 암시하지 말고 state 타입으로 표현한다.

## Side Effect

- navigation, snackbar, toast, dialog 요청처럼 일회성 이벤트는 side effect로 전달한다.
- side effect 이름은 UI가 해야 할 일을 기준으로 작성한다.
- repository error는 side effect로 전달하고, resource 변환은 UI에서 처리한다.

## Intent와 Reduce

- public 함수는 UI action과 1:1로 읽히게 작성한다.
- 상태 변경은 `intent { reduce { ... } }` 안에서 수행한다.
- 특정 state에서만 변경해야 하면 `reduceOn<FeatureUiState.Success> { ... }`를 사용한다.
- 여러 비동기 작업을 동시에 시작해야 하면 기존 `vmAsync` 패턴을 따른다.
- `Result.getOrNull()!!`처럼 강한 가정이 필요한 코드는 바로 앞에서 실패 조건을 명확히 검증한다.

## 금지 사항

- ViewModel에서 Compose API를 호출하지 않는다.
- ViewModel에서 Android resource를 직접 읽지 않는다.
- ViewModel에서 DTO나 remote response 타입을 다루지 않는다.
- 네트워크 예외 타입을 그대로 UI state에 오래 보관하지 않는다.

