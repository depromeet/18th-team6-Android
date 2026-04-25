# Android Compose Convention

Android UI는 Compose 기반으로 작성하며 `android/*` 모듈에 둔다. shared 모듈은 Android Compose UI에 의존하지 않는다.

## 파일 배치

- 화면 단위 코드는 화면 모듈의 `screen` 패키지에 둔다.
- 진입점 컴포저블은 `FeatureScreen.kt` 형식을 사용한다.
- 상태별 콘텐츠는 필요하면 `FeatureScreenContent.kt`, `FeatureScreenLoadingContent.kt`, `FeatureScreenFailureContent.kt`, `FeatureScreenSuccessContent.kt`처럼 분리한다.
- 재사용 가능한 Android UI 유틸은 `android:core:ui`에 둔다.

## Screen 작성

- `Screen` 컴포저블은 ViewModel 연결, state 수집, side effect 처리, action 연결을 담당한다.
- 실제 화면 렌더링은 `ScreenContent` 계열 컴포저블이 담당한다.
- `ScreenContent`에는 상태와 action을 명시적으로 전달한다.
- repository, remote data source, Ktor client를 UI에서 직접 주입하지 않는다.
- `Modifier`는 기본 인자로 받고 최상위 레이아웃에 전달한다.

```kotlin
@Composable
fun AgentScreen(
    modifier: Modifier = Modifier,
    viewModel: AgentViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    AgentScreenContent(
        state = state,
        action = AgentScreenAction(
            onCreateAgent = viewModel::createAgent,
            onDeleteAgent = viewModel::deleteAgent,
            onPatchAgent = viewModel::patchAgent,
            onAgentClick = viewModel::onAgentClick,
            onAgentLongClick = viewModel::onAgentLongClick,
            onMenuClick = viewModel::onMenuClick,
        ),
        modifier = modifier,
    )
}
```

## Action 객체

- 화면 이벤트는 `internal data class FeatureScreenAction`으로 묶는다.
- action은 UI 이벤트 이름으로 작성한다.
- action 안에서 비즈니스 로직을 실행하지 않는다.
- action 타입은 화면에 필요한 최소 인자만 받는다.

## State 렌더링

- state는 ViewModel의 sealed interface를 그대로 받아 분기한다.
- loading, failure, success UI는 명확히 분리한다.
- error message 변환처럼 Android resource가 필요한 일은 UI 계층에서 처리한다.
- domain model 자체를 보여줘도 되지만 UI 전용 포맷이 복잡해지면 별도 UI model을 고려한다.

## Preview

- preview는 가능한 `ScreenContent` 대상으로 작성한다.
- preview에서 ViewModel, Koin, 네트워크에 의존하지 않는다.
- sample state와 fake action을 사용한다.

