# Dependency Injection Convention

OBRit은 Koin을 사용한다. shared module은 KMP 공통 DI를 제공하고, Android app은 Android context와 feature module을 조립한다.

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

## Android DI

- Android app 시작 시 `ObritApplication`에서 `initKoin`을 호출한다.
- Android context가 필요하면 `androidContext(...)`를 app declaration에서 등록한다.
- feature ViewModel은 각 feature module의 DI 파일에서 등록한다.
- feature module은 app module에서 조립한다.

```kotlin
val agentFeatureModule =
    module {
        viewModelOf(::AgentViewModel)
    }
```

## 등록 규칙

- 인터페이스가 있는 구현체는 인터페이스 타입으로 등록한다.
- stateless repository와 data source는 기본적으로 `single`을 사용한다.
- ViewModel은 `viewModelOf`를 사용한다.
- 새 module을 만들면 module name은 `featureNameModule` 또는 `layerNameModule`처럼 소문자 camelCase로 작성한다.
- DI 등록만을 위해 구현체 visibility를 public으로 넓히지 않는다.

## 금지 사항

- UI에서 repository 구현체를 직접 생성하지 않는다.
- repository에서 remote data source 구현체를 직접 생성하지 않는다.
- shared DI module에서 Android `Context`를 요구하지 않는다.

