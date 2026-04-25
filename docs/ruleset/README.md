# Convention Documents

이 디렉터리는 OBRit 코드 작성 규칙을 컴포넌트 단위로 정리한다. 새 기능을 만들거나 기존 코드를 수정할 때는 변경 대상과 가장 가까운 문서를 먼저 확인한다.

## 문서 목록

- [UI.md](UI.md) - Android Compose 화면, 콘텐츠 컴포저블, 액션 객체 규칙
- [ViewModel.md](ViewModel.md) - Orbit MVI 기반 ViewModel, state, side effect 규칙
- [Repository.md](Repository.md) - shared data repository 인터페이스와 구현 규칙
- [Network.md](Network.md) - Ktor remote data source, DTO, HTTP client 규칙
- [Model.md](Model.md) - shared model, domain type, typed error 규칙
- [DependencyInjection.md](DependencyInjection.md) - Koin module 구성과 등록 위치 규칙
- [GradleModule.md](GradleModule.md) - Gradle module, convention plugin, dependency 선언 규칙

## 공통 원칙

- 현재 모듈 경계를 우선한다.
- Android UI 코드는 `android/*`에 둔다.
- 공통 domain/data/network 코드는 `shared/*`에 둔다.
- 플랫폼별 구현은 KMP source set(`androidMain`, `iosMain`)에 둔다.
- public API는 필요한 만큼만 열고, 구현체는 가능한 `internal`로 유지한다.
- 새 추상화는 중복을 줄이거나 기존 패턴과 맞을 때만 추가한다.
- TODO를 새로 만들 때는 후속 작업이 명확한 경우에만 남긴다.

