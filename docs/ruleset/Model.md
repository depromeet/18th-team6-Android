# Model Convention

Model 계층은 `shared:model`에 둔다. 이 계층은 platform UI, Ktor DTO, persistence 구현을 알지 않는다.

## Domain Model

- domain model은 Kotlin data class로 작성한다.
- 값은 가능하면 non-null로 유지한다.
- 서버 raw string, raw timestamp 등은 network mapper에서 domain 타입으로 변환한다.
- 시간 값은 현재 패턴처럼 `kotlin.time.Instant`를 사용한다.
- domain enum에는 알 수 없는 서버 값을 받을 수 있는 `UNKNOWN` 같은 fallback을 둔다.

```kotlin
data class Agent(
    val id: Int,
    val name: String,
    val description: String,
    val timestamp: Instant,
    val type: AgentType,
)
```

## Params Model

- 여러 값을 묶어 전달하는 수정 요청은 params model로 만든다.
- params model은 repository API에 필요한 domain 타입만 포함한다.
- network request DTO를 params model로 재사용하지 않는다.

## Error Model

- 서버 error code와 연결되는 domain error는 `RootError`를 상속한다.
- error group은 use case 또는 API 동작 기준으로 나눈다.
- 각 구체 error는 하위 class로 표현하고 `code`를 override한다.
- `createErrorInstances()`에는 매핑 가능한 error를 모두 등록한다.

```kotlin
open class CreateAgentError : RootError() {
    class EmptyName : CreateAgentError() {
        override val code = 10000
    }

    override fun createErrorInstances(): Array<RootError> =
        arrayOf(EmptyName())
}
```

## 금지 사항

- model에서 platform resource, UI framework, Ktor response/request 타입을 참조하지 않는다.
- model에서 DI module을 정의하지 않는다.
- 서버 DTO의 nullable 구조를 domain model에 그대로 전파하지 않는다.
