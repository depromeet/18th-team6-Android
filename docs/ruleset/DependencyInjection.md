# Dependency Injection Convention

OBRit은 Koin을 사용한다. 이 문서는 shared module에서 Android와 iOS가 함께 쓰는 공통 DI 규칙만 다룬다. 플랫폼 app 조립 규칙은 `docs/android/DependencyInjection.md`와 `docs/ios/DependencyInjection.md`를 따른다.

## Shared DI

- shared network 등록은 `shared:network`의 `NetworkModule.kt`에 둔다.
- shared data 등록은 `shared:data`의 `DataModule.kt`에 둔다.
- shared module 집계는 `shared/src/commonMain/.../di/SharedKoin.kt`의 `sharedModules()`에서 한다.
- shared DI는 Android-only 타입에 의존하지 않는다.

```kotlin
fun sharedModules(): List<Module> =
    listOf(
        networkModule,
        dataModule,
    )
```

## 등록 규칙

- 인터페이스가 있는 구현체는 인터페이스 타입으로 등록한다.
- stateless repository와 data source는 기본적으로 `single`을 사용한다.
- shared module name은 `networkModule`, `dataModule`처럼 layer 이름을 기준으로 소문자 camelCase로 작성한다.
- DI 등록만을 위해 구현체 visibility를 public으로 넓히지 않는다.

## 금지 사항

- presentation layer에서 repository 구현체를 직접 생성하지 않는다.
- repository에서 remote data source 구현체를 직접 생성하지 않는다.
- shared DI module에서 Android `Context`를 요구하지 않는다.
