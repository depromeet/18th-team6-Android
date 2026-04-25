# Common Ruleset Documents

이 디렉터리는 Android와 iOS가 함께 따라야 하는 공통 규칙을 정리한다. 플랫폼 presentation layer 전용 규칙은 `docs/android/`와 `docs/ios/`를 먼저 확인한다.

## 문서 목록

- [Repository.md](Repository.md) - shared data repository 인터페이스와 구현 규칙
- [Network.md](Network.md) - Ktor remote data source, DTO, HTTP client 규칙
- [Model.md](Model.md) - shared model, domain type, typed error 규칙
- [DependencyInjection.md](DependencyInjection.md) - shared Koin module 구성과 등록 위치 규칙
- [GradleModule.md](GradleModule.md) - Gradle module, convention plugin, dependency 선언 규칙
- [Git.md](Git.md) - branch, commit, PR, GitHub Issue 작업 규칙

## 플랫폼 문서

- [Android Presentation Rules](../android/README.md)
- [iOS Presentation Rules](../ios/README.md)

## 공통 원칙

- 현재 모듈 경계를 우선한다.
- 공통 domain/data/network 코드는 `shared/*`에 둔다.
- 플랫폼 presentation code는 각 플랫폼 문서의 배치 규칙을 따른다.
- 플랫폼별 KMP 구현은 해당 source set에 둔다.
- public API는 필요한 만큼만 열고, 구현체는 가능한 `internal`로 유지한다.
- 새 추상화는 중복을 줄이거나 기존 패턴과 맞을 때만 추가한다.
- TODO를 새로 만들 때는 후속 작업이 명확한 경우에만 남긴다.
