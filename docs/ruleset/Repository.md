# Repository Convention

Repository는 shared data 계층의 public API이다. Presentation layer는 remote data source 대신 repository에 의존한다.

## 파일 배치

- repository 인터페이스는 `shared:data/src/commonMain/.../repository`에 둔다.
- 구현체는 같은 패키지에 두고 `internal class`로 작성한다.
- repository가 반환하는 domain model은 `shared:model`에 둔다.
- network request/response 타입은 repository API 밖으로 노출하지 않는다.

## API 규칙

- suspend 함수는 실패 가능성을 `Result<T>`로 표현한다.
- 생성, 수정, 삭제 함수명은 현재 패턴처럼 `createX`, `patchX`, `deleteX`를 사용한다.
- 조회 함수명은 단건 `getX`, 목록 `getXs`를 사용한다.
- 복잡한 수정 파라미터는 `shared:model`의 params data class로 묶는다.

```kotlin
interface AgentRepository {
    suspend fun getAgent(id: Int): Result<Agent>
    suspend fun getAgents(): Result<List<Agent>>
    suspend fun createAgent(
        name: String,
        description: String,
        type: AgentType,
    ): Result<Agent>
}
```

## 구현 규칙

- remote 호출은 remote data source에 위임한다.
- DTO to domain 변환은 repository 구현에서 수행한다.
- known remote error는 `runCatchingWith(FeatureError())`로 typed error에 매핑한다.
- `CancellationException`은 삼키지 않는다. 기존 `runCatchingWith`가 이 규칙을 처리한다.
- repository 구현체는 Koin module에서 인터페이스 타입으로 등록한다.

```kotlin
internal class AgentRepositoryImpl(
    private val agentRemoteDataSource: AgentRemoteDataSource,
) : AgentRepository {
    override suspend fun getAgents(): Result<List<Agent>> =
        runCatchingWith(GetAgentsError()) {
            agentRemoteDataSource.getAgents().agents.map { it.toAgent() }
        }
}
```

## 책임 경계

- Repository는 UI 문구, platform resource, presentation state를 알지 않는다.
- Repository는 HTTP path를 직접 조합하지 않고 remote data source를 사용한다.
- Repository는 domain model 기준으로 성공 값을 반환한다.
- 캐시, 로컬 DB, pagination이 추가되면 repository가 조율하되 API는 domain 중심으로 유지한다.
