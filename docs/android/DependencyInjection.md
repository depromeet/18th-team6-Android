# Android Dependency Injection Convention

Android presentation layer는 Koin으로 ViewModel과 화면 모듈을 조립한다. shared DI의 공통 규칙은 `docs/ruleset/DependencyInjection.md`를 따른다.

## App 조립

- Android app 시작 시 `ObritApplication`에서 `initKoin`을 호출한다.
- Android context가 필요하면 `androidContext(...)`를 app declaration에서 등록한다.
- Android 화면/예시 모듈은 app module에서 명시적으로 추가한다.

```kotlin
initKoin {
    androidContext(this@ObritApplication)
    modules(agentFeatureModule)
}
```

## 화면 모듈 DI

- 화면 모듈의 DI 파일은 해당 모듈의 `di` 패키지에 둔다.
- ViewModel은 `viewModelOf(::FeatureViewModel)`로 등록한다.
- module name은 `featureNameModule` 또는 `screenNameModule`처럼 소문자 camelCase로 작성한다.
- DI 등록만을 위해 ViewModel 생성자나 구현체 visibility를 public으로 넓히지 않는다.

```kotlin
val agentFeatureModule =
    module {
        viewModelOf(::AgentViewModel)
    }
```

## 금지 사항

- Compose UI에서 repository 구현체를 직접 생성하지 않는다.
- ViewModel에서 remote data source 구현체를 직접 생성하지 않는다.
- shared DI module에서 Android `Context`를 요구하지 않는다.

