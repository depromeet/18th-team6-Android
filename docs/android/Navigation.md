# Android Navigation Convention

Android navigation은 Navigation3 기반으로 작성하며 `android:app` 모듈에서 앱 전체 화면 흐름을 조립한다. 각 feature 모듈은 화면과 ViewModel을 제공하고, 실제 back stack 변경은 navigation 계층에서 수행한다.

## 파일 배치

- 앱 전체 navigation entry point는 `android/app/src/main/kotlin/com/obrit/obrit/navigation/OBRitNavigation.kt`에 둔다.
- feature 단위 navigation graph는 같은 패키지의 `FeatureNavigation.kt` 형식으로 둔다.
- route key는 `navigation/route` 패키지에 둔다.
- 모든 route key는 `Route : NavKey`를 구현하고 `@Serializable`을 붙인다.
- feature 모듈에서 navigation을 의존하지 않는다.

## 현재 구조

```text
MainActivity
+ OBRitNavigation
  + NavDisplay(root back stack)
    + entry<AgentRoute>
      + AgentNavigation
        + NavDisplay(agent back stack)
          + AgentRoute.Agents -> AgentScreen
          + AgentRoute.AgentDetail -> Agent detail screen
```

`MainActivity`는 theme 아래에서 `OBRitNavigation()`만 호출한다. 화면 전환, route 등록, back stack 조작은 `OBRitNavigation` 또는 feature navigation 컴포저블에서 처리한다.

## Root Navigation

- `OBRitNavigation`은 앱 최상위 `NavDisplay`와 root back stack을 소유한다.
- root entry는 feature route group 단위로 등록한다.
- root navigation은 feature 화면을 직접 렌더링하지 않고 `FeatureNavigation`으로 위임한다.
- app shell, bottom navigation, top-level tab처럼 여러 feature를 묶는 UI가 생기면 root navigation에서 소유한다.
- root back stack에는 앱 섹션 전환에 필요한 key만 둔다. feature 내부 상세 화면 흐름은 feature navigation의 back stack에 둔다.

```kotlin
entry<AgentRoute> {
    AgentNavigation(modifier = Modifier)
}
```

## Feature Navigation

- feature navigation은 해당 feature flow의 시작 route를 명시한다.
- feature 내부 화면 전환은 feature navigation의 back stack을 변경한다.
- 화면 컴포저블에는 navigation callback만 전달하고, `ScreenContent`에는 route나 back stack을 직접 전달하지 않는다.
- ViewModel이 navigation 결정을 내려야 하는 경우 side effect를 발생시키고, `Screen`이 이를 외부 callback으로 변환한다.
- 상세 화면처럼 뒤로 가기가 필요한 flow가 추가되면 feature `NavDisplay`에서 feature back stack 기준의 back 처리를 연결한다.

```kotlin
val agentBackStack = rememberNavBackStack(AgentRoute.Agents)

entry<AgentRoute.Agents> {
    AgentScreen(
        onAgentClick = { agent ->
            agentBackStack.add(AgentRoute.AgentDetail(agent.id))
        },
        modifier = Modifier,
    )
}
```

## Route Key

- feature route group은 `sealed interface FeatureRoute : Route`로 작성한다.
- 시작 화면처럼 argument가 없는 route는 `data object`를 사용한다.
- 상세 화면처럼 argument가 필요한 route는 `data class`를 사용한다.
- route argument에는 stable id, filter, mode처럼 화면 복원에 필요한 최소 값만 담는다.
- domain model 전체, repository, Android `Context`, resource id를 route key에 넣지 않는다.

```kotlin
@Serializable
sealed interface AgentRoute : Route {
    @Serializable
    data object Agents : AgentRoute

    @Serializable
    data class AgentDetail(
        val id: Int,
    ) : AgentRoute
}
```

## 새 Feature 추가

- `navigation/route` 패키지에 `FeatureRoute : Route`를 추가한다.
- `FeatureNavigation.kt`를 만들고 시작 route로 `rememberNavBackStack(...)`을 초기화한다.
- `OBRitNavigation`의 `entryProvider`에 `entry<FeatureRoute> { FeatureNavigation(...) }`을 등록한다.
- feature 화면은 외부 navigation callback을 받고, 내부 UI 이벤트는 기존 `ScreenAction` 규칙을 따른다.
- app module에 feature dependency가 필요하면 `implementation(projects.android.feature.featureName)` 형식의 type-safe accessor를 사용한다.

## Verification

- navigation code나 app wiring을 바꿨다면 `:android:app:compileDebugKotlin`을 우선 확인한다.
- 특정 feature 화면만 바꿨다면 해당 feature의 `compileDebugKotlin` task를 먼저 확인한다.
- 문서만 변경한 경우 Gradle build는 생략할 수 있으며, 생략 이유를 작업 결과에 남긴다.
