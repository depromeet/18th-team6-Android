# Network Convention

Network 계층은 `shared:network`에 있으며 Ktor와 kotlinx.serialization을 사용한다. Android와 iOS HTTP engine은 KMP `expect`/`actual`로 분리한다.

## 파일 배치

- remote data source 인터페이스와 구현은 `source` 패키지에 둔다.
- request DTO는 `request/<domain>` 패키지에 둔다.
- response DTO와 mapper는 `response/<domain>` 패키지에 둔다.
- HTTP client, Json, platform engine은 `client` 패키지에 둔다.
- network DI 등록은 `di/NetworkModule.kt`에 둔다.

## Remote Data Source

- 인터페이스는 feature/domain별로 분리한다.
- 구현체는 `internal class`로 작성한다.
- public 함수는 response DTO 또는 `Unit`을 반환한다.
- repository가 아닌 remote data source에서 HTTP method와 path를 관리한다.
- path는 기존 스타일처럼 relative path를 사용한다.

```kotlin
interface AgentRemoteDataSource {
    suspend fun getAgent(id: Int): AgentResponse
    suspend fun getAgents(): AgentsResponse
    suspend fun createAgent(request: CreateAgentRequest): AgentResponse
}
```

## DTO와 Serialization

- DTO는 `@Serializable` data class로 작성한다.
- 서버 필드명은 `@SerialName`으로 명시한다.
- 서버 응답에서 nullable할 수 있는 값은 DTO에서 nullable로 받는다.
- domain model에는 가능한 non-null 값을 제공하고, fallback은 mapper에서 결정한다.
- enum-like string은 mapper에서 domain enum으로 변환한다.

## HTTP Client

- 공통 설정은 `configureObritHttpClient`에 둔다.
- base URL, logging 여부는 `NetworkConfiguration`을 통해 주입한다.
- `Json` 설정은 `createJson()`에서 관리한다.
- 실패 응답은 `HttpResponseValidator`에서 `RemoteError`로 변환한다.
- platform engine 추가가 필요하면 `expect fun platformHttpClientEngineFactory()`의 actual 구현을 추가한다.

## Error Handling

- HTTP 실패 body는 `NetworkErrorResponse`로 파싱한다.
- 서버 error code는 repository에서 `RootError` 하위 타입으로 변환한다.
- 알 수 없는 error code는 원래 `RemoteError`를 유지한다.

