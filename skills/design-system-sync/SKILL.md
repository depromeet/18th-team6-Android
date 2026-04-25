---
name: design-system-sync
description: |
  Figma SSOT의 토큰/컴포넌트/에셋 변경을 OBRit 안드로이드 코드(shared/design-system, android/core/design-system)에 반영하는 동기화 스킬.
  이 스킬은 다음과 같은 요청에 반드시 사용한다: "디자인 동기화", "디자인 시스템 동기화", "토큰 반영", "Figma 변경 반영", "디자이너 코멘트 반영", "디자인 시스템 업데이트", "Atom 토큰 갱신", "Semantic 토큰 갱신", "/design-system-sync".
  디자이너가 SSOT에 변경사항과 코멘트를 남겼고 그것을 코드로 옮겨야 하는 맥락이면 이 스킬을 사용한다.
user_invocable: true
---

# 디자인 시스템 동기화

Figma SSOT(디자이너 원본) → Dev 복제본 → 안드로이드 코드의 단방향 흐름을 따라 디자이너 변경사항을 코드에 반영한다.

운영 모델·구조의 상세 배경은 `docs/design/design-system-readme.md` 를 따른다. 이 스킬은 그 절차의 실행 도우미다.

## 전제

- **이 스킬은 OBRit 레포 전용**이다. 다른 레포에서는 트리거하지 않는다.
- **SSoT는 Figma의 Local Variables뿐**이다. Local Styles(Text Style, Effect Style 등)는 동기화 대상에서 제외한다. `get_variable_defs` 결과에 Style 항목이 섞여 와도 코드에 반영하지 않는다.
- 디자이너가 SSOT의 변경 영역에 **Figma 코멘트로 변경 사항을 알린 상태**여야 한다.
- 게이트키퍼(개발자 1명)가 **Dev 복제본에 미러링까지 마친 상태**여야 한다. 미러링 자체는 Figma UI에서 사람이 수행하는 작업이므로 이 스킬은 미러링 이후의 코드 반영을 자동화한다.
- Figma MCP(`mcp__figma__*`) 가 활성화되어 있어야 한다.

## 인자

- `$ARGUMENTS`: 동기화 종류 — `token`, `component`, `asset` 중 하나. 생략 시 사용자에게 묻는다.

## 실행 순서

### STEP 1: 컨텍스트 확인

다음을 사용자에게 묻거나 추론한다.

- 동기화 종류(`token` / `component` / `asset`)
- Dev 복제본의 **fileKey**와 **nodeId**(특정 영역만 동기화 시)
- 디자이너 SSOT 코멘트 링크(커밋 메시지와 답글에 사용)

이미 사용자가 메시지에 알려준 정보가 있다면 다시 묻지 않는다.

### STEP 2: Dev 복제본에서 MCP로 변경 대상 조회

종류별 분기:

- **token**: `mcp__figma__get_variable_defs(fileKey, nodeId)` 호출.
  - 결과에서 **Variables만 채택**하고 **Styles는 무시**한다 (Variables-only SSoT 정책).
  - 식별 기준: 값이 hex 컬러(`#xxxxxx`), 숫자, 문자열, 불리언 → **Variable**. 값이 `Font(...)` 형태 → **Text Style**(무시). 묶음 정의 형태도 Style이므로 무시.
- **component**: `mcp__figma__get_design_context(fileKey, nodeId)` 호출. 필요 시 `mcp__figma__get_screenshot`로 시각 확인.
- **asset**: `mcp__figma__get_design_context` 또는 사용자가 export한 SVG/PNG 경로 확인.

### STEP 3: 코드 갱신

#### token인 경우

1. Atom 토큰 갱신: `shared/design-system/src/commonMain/kotlin/com/obrit/obrit/shared/designsystem/tokens/atom/**/*.kt`
   - Color는 `tokens/atom/color/AtomColors.kt`에 ARGB `Long`(0xAARRGGBB)으로 추가/수정.
   - 그 외 Atom(size, typography 등)은 `tokens/atom/{kind}/Atom{Kind}.kt`에 동일 패턴.
   - **Compose `Color` 등 플랫폼 타입 import 절대 금지** (commonMain 플랫폼 중립성 유지).
2. Semantic 토큰 갱신: `shared/design-system/src/commonMain/kotlin/com/obrit/obrit/shared/designsystem/tokens/semantic/{Kind}.kt`
   - Atom을 참조하는 형태로만 정의. raw 값 직접 박지 않는다.
3. 명명 규칙은 Figma 경로를 객체 트리로 변환:
   - Figma `Blue/500` → `AtomColors.Blue.S500`
   - Figma `Brand/Primary` → `SemanticColors.Brand.Primary`

#### component인 경우

1. `android/core/design-system/src/main/java/com/obrit/android/core/designsystem/component/` 아래에 Compose 컴포넌트 추가/수정.
2. 컴포넌트는 **Semantic 토큰을 직접 소비**한다. 별도 Component-specific 토큰 층을 두지 않는다.
3. Theme 어댑터(`theme/AppTheme.kt`)를 거쳐 Compose 타입(`Color`, `Dp`, `TextStyle`, `Shape`)으로 변환된 토큰을 사용. commonMain 토큰을 화면 코드에서 직접 import 금지.

#### asset인 경우

1. SVG는 `android/core/design-system/src/main/res/drawable/`에 Vector Asset으로 변환해 저장 또는 Compose `ImageVector` 코드로 변환.
2. 비트맵은 `drawable-*` / `mipmap-*` 아래 적절한 dpi에.
3. 명명 규칙: Figma 이름 1:1 매칭 (`Icon/Arrow/Right` → `ic_arrow_right`).

### STEP 4: 빌드 검증

```bash
bash ./gradlew :shared:design-system:assemble :android:core:design-system:assemble
```

- `BUILD SUCCESSFUL` 확인.
- 실패 시 에러를 사용자에게 보여주고 수정한다. **임시 우회 금지**(예: 의존성 빼기, raw 값 박기). 근본 원인 수정.
- commonMain 플랫폼 중립성 위반 검출:
  ```bash
  grep -r "androidx\." /Users/elaus/AndroidStudioProjects/18th-team6-Android/shared/design-system/src/commonMain/ || echo "OK: commonMain 중립성 유지"
  ```
  매칭이 있으면 즉시 수정.

### STEP 5: 커밋

- 변경 파일만 staging:
  ```bash
  git add shared/design-system android/core/design-system
  ```
- 커밋 메시지에 **SSOT 코멘트 링크 포함**:
  ```
  feat(design-system): {요약} (figma comment: {SSOT_COMMENT_URL})
  ```
- 푸시는 사용자가 명시적으로 요청한 경우에만.

### STEP 6: SSOT 답글 텍스트 생성

게이트키퍼가 SSOT 디자이너 코멘트에 직접 답글을 달 수 있도록 다음 형식 텍스트를 출력한다.

```
공용 토큰 반영 완료
- 커밋: {SHORT_SHA}
- 파일: {갱신된 주요 파일 경로}
```

답글 자체는 사람이 Figma UI에서 단다(자동 답글 API는 사용하지 않음).

## 출력 형식

```markdown
## 디자인 시스템 동기화 완료

**대상**: token / component / asset
**Dev 복제본**: {fileKey} / {nodeId}

**변경된 파일**
- {경로 1}
- {경로 2}

**빌드**: ✅ BUILD SUCCESSFUL
**commonMain 중립성**: ✅

**커밋**: {SHORT_SHA} {메시지}

**SSOT 답글용 텍스트**
> 공용 토큰 반영 완료
> - 커밋: {SHORT_SHA}
> - 파일: ...
```

## 금지 사항

- ❌ **Figma Local Styles는 코드에 반영 금지**. SSoT는 Variables뿐. Text Style, Effect Style, Color Style 등은 `get_variable_defs` 결과에 섞여 와도 무시한다.
- ❌ `tokens.json`이나 중간 산출물 파일 생성하지 않는다.
- ❌ Style Dictionary, Tokens Studio 같은 외부 변환 도구 도입하지 않는다.
- ❌ Figma Code Connect 매핑 파일 생성하지 않는다.
- ❌ Component-specific 토큰 층 만들지 않는다 — 컴포넌트가 Semantic 직접 소비.
- ❌ commonMain 토큰 코드에서 Compose/SwiftUI 등 플랫폼 타입 import하지 않는다.
- ❌ 양방향 동기화 시도 금지 — 코드 → Figma 경로 없음.
- ❌ SSOT 파일 직접 MCP 호출 금지 — Dev 복제본만 대상.

## 관련 문서

- `docs/design/design-system-readme.md` — 운영 가이드(디자이너·개발자 공통 참조).
- `.claude/plans/figma-dev-glowing-badger.md` — 의사결정 배경.
