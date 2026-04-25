# 디자인 시스템 운영 가이드

## 이 문서의 목적

디자이너의 Figma SSOT 파일과 코드(Android Compose, KMP shared) 사이의 동기화 절차를 정의한다.
디자이너·개발자가 같은 어휘로 같은 절차를 따르도록 하는 것이 목표다.

---

## 배경

- **Figma SSOT 파일**: 디자이너가 작업하는 원본. 디자이너 계정 플랜 한계로 **Dev Mode 비활성**.
- **Figma Dev 복제본**: SSOT를 복제한 별도 파일. **Dev Mode 활성**. 코드 구현용 작업 공간.
- 두 파일은 같은 Figma 팀이지만 **라이브러리 퍼블리시 불가** → 자동 구독 동기화 사용 못 함.
- 따라서 **사람이 매개하는 단방향 흐름**으로 운영한다.

---

## 동기화 흐름

```
┌─────────────────┐     코멘트     ┌──────────────────┐                    ┌──────────────────────┐
│  디자이너 SSOT  │ ─────────────► │   개발자 1명     │                    │   코드               │
│   (원본)        │                │  (게이트키퍼)    │                    │ shared/design-system  │
│ Variables /     │ ◄───  답글  ── │                  │                    │ (Atom + Semantic)     │
│ 컴포넌트 /      │ "옮김 + 커밋   └────────┬─────────┘                    │ android/core/         │
│ 에셋            │   해시"                 │                              │   design-system       │
└─────────────────┘                         │  미러링                      │ (어댑터 + 컴포넌트)   │
                                            ▼                              └─────────▲──────────┘
                                  ┌──────────────────┐                               │
                                  │  Dev 복제본      │                               │
                                  │  (Dev Mode 활성) │                               │
                                  └────────┬─────────┘                               │
                                           │  Figma MCP                              │
                                           └─────────────────────────────────────────┘
```

- **SSOT는 진실의 원천**. 코드·Dev 복제본 모두 SSOT를 따라간다.
- **자동화는 전부 Dev 복제본을 대상으로**. SSOT를 MCP로 직접 읽지 않는다.
- **양방향 동기화는 시도하지 않는다.** 코드에서 Figma로 거꾸로 흐르는 경로 없음.

---

## 역할

### 디자이너
- SSOT 파일에서 토큰·컴포넌트·에셋 추가/수정.
- 변경한 영역에 **Figma 코멘트로 변경 사항 알림** (어떤 토큰이 추가/변경됐는지, 어떤 컴포넌트가 어떻게 바뀌었는지).
- 코드 결과를 보고 싶으면 Dev 복제본을 본다(SSOT에는 코드 정보 없음).

### 개발자 1명 (게이트키퍼)
- SSOT의 미응답 코멘트를 주기적으로 확인.
- 디자이너 코멘트를 받아 **Dev 복제본에 미러링**.
- Dev 복제본에서 **Figma MCP로 토큰/컴포넌트 읽어 코드에 반영**.
- 처리 완료 시 **SSOT의 해당 디자이너 코멘트에 답글**로 "옮김 + 커밋 해시/링크" 표시.
- 백업 인원 1명 합의 필요(휴가/이탈 대비).

---

## 토큰 구조 (Atom + Semantic 2단계)

토큰은 **Atom → Semantic 두 층**으로만 구성한다.

### Atom (원시 값)
- 의미 없는 순수 값. 디자인 팔레트.
- SSOT에서 Color는 별도 컬렉션으로 분리되어 있고(다른 Atom과 별개 컬렉션), 코드도 같은 분리를 따른다.
- 필요시 Semantic을 거치지 않고 바로 Atom을 사용할 수 있다.
- 예: `Blue/500 = #0064FF`, `Gray/900 = #191F28`, `Space/8 = 8`.

### Semantic (의미 기반)
- Atom을 참조하되 "용도"로 명명.
- 예: `Brand.Primary = Blue/500`, `Text.OnSurface.Default = Gray/900`, `Surface.Default = White`.
- **디자인 변경 시 교체 지점**. 브랜드 색을 바꾸면 Semantic 한 줄만 바꾸면 화면 전반에 반영.

### Component (토큰 아님, 실제 구현)
- 별도 토큰 층을 두지 않는다.
- 컴포넌트 코드가 **Semantic 토큰을 직접 소비**한다.
- 예: `PrimaryButton`은 `Brand.Primary`를 직접 참조해 배경색을 칠함.

---

## 코드 위치

| 자산 | 위치 | 비고 |
|---|---|---|
| Atom 토큰 | `shared/design-system/src/commonMain/kotlin/.../tokens/atom/` | 플랫폼 중립 값(Long/Float/String). Compose·SwiftUI 타입 의존 금지 |
| Semantic 토큰 | `shared/design-system/src/commonMain/kotlin/.../tokens/semantic/` | Atom 참조. 라이트/다크 모드 처리 포함 |
| Android Theme 어댑터 | `android/core/design-system/.../theme/` | 공용 토큰 → Compose `Color`/`TextStyle`/`Dp`/`Shape` 변환 |
| Android 컴포넌트 | `android/core/design-system/.../component/` | Semantic 토큰을 직접 소비 |
| 벡터 에셋 | `android/core/design-system/src/main/res/drawable/` | 또는 Compose `ImageVector` |
| 비트맵 에셋 | 동일 모듈의 `drawable-*` / `mipmap-*` | |

iOS 컴포넌트는 같은 공용 토큰을 받아 SwiftUI로 자체 구현(본 레포 범위 밖).

---

## 절차

### 토큰 변경
1. 디자이너가 SSOT Variables 수정 + 해당 영역에 코멘트.
2. 게이트키퍼가 Dev 복제본에 동일 이름의 Variables 미러링.
3. Figma MCP `get_variable_defs`로 Dev 복제본 읽음.
4. 공용 Atom/Semantic 토큰 코드(commonMain) 수동 갱신.
5. Android 빌드 확인. 어댑터 변경 필요 없는 게 정상(값 갱신만 흘러야 함).
6. 커밋 + 푸시 → SSOT 디자이너 코멘트에 **답글로 커밋 해시/링크와 함께 "공용 토큰 반영 완료"** 표시.

### 컴포넌트 변경
1. 디자이너가 SSOT 컴포넌트 수정/추가 + 코멘트.
2. 게이트키퍼가 Dev 복제본에 컴포넌트 미러링.
3. Figma MCP `get_design_context`로 컴포넌트 노드 읽음.
4. `android/core/design-system/component/` 아래 Compose 컴포넌트 추가/수정. **Semantic 토큰을 직접 참조**.
5. 빌드/Preview 확인. 샘플 화면 적용 검증.
6. 커밋 + 푸시 → SSOT 코멘트 답글.

### 에셋 변경
1. 디자이너가 SSOT에 에셋 추가/수정 + 코멘트.
2. 게이트키퍼가 Dev 복제본에 미러링 후 SVG/PNG export.
3. 적절한 위치에 저장(상단 표 참조). 명명 규칙: Figma 이름 1:1 매칭(`Icon/Arrow/Right` → `ic_arrow_right`).
4. 커밋 + 푸시 → SSOT 코멘트 답글.

---

## 명명 규칙

| 대상 | 규칙 | 예 |
|---|---|---|
| Atom 토큰(코드) | Figma 경로를 객체 트리로 매핑 | `AtomColors.Blue.S500` |
| Semantic 토큰(코드) | "용도" 중심 명명 | `SemanticColors.Brand.Primary` |
| Compose 컴포넌트 | 파스칼 케이스, Figma 컴포넌트명 따라감 | `PrimaryButton` |
| 에셋 파일 | 스네이크 케이스, 카테고리 접두사 | `ic_arrow_right`, `img_logo` |
| 커밋 메시지 | SSOT 디자이너 코멘트 링크 포함 | `feat(design-system): Brand.Primary 갱신 (figma comment: <URL>)` |

---

## 사용하지 않는 것들 (의도적 제외)

- ❌ **Tokens Studio for Figma 플러그인** — 자동화 도구 도입 안 함
- ❌ **Style Dictionary / 별도 빌드 변환기** — 중간 산출물 두지 않음
- ❌ **`tokens.json` 같은 중간 파일** — 게이트키퍼가 MCP로 직접 코드 갱신
- ❌ **Figma Code Connect** — 매핑 유지 비용 회피
- ❌ **Component-specific 토큰 층** — 컴포넌트가 Semantic 직접 소비
- ❌ **별도 Snapshot Log 페이지/문서** — 기록은 SSOT 디자이너 코멘트의 답글로만
- ❌ **양방향 동기화** — 단방향(SSOT → Dev 복제본 → 코드)만

---

## 미정 사항 (운영하면서 결정)

- **동기화 주기**: 코멘트 즉시 처리 / 작업 트리거 단위 pull / 스프린트 단위 일괄 중 무엇이 적합한지.
- **다크 모드 처리 위치**: 공용(commonMain `mode` 파라미터) vs Android 어댑터(`MaterialTheme`의 light/darkColorScheme). 첫 토큰 옮길 때 디자이너의 SSOT 모드 정의 방식을 보고 결정.

---

## 리스크 요약

- **게이트키퍼 1명 의존**: 휴가/이탈 시 동기화 중단. 백업 인원 합의 + 미응답 코멘트 정기 점검 습관으로 완화.
- **변경 사유 추적**: 중간 JSON 없으므로 코드만 봐서는 "왜 바뀌었는지" 알기 어려움. 커밋 메시지에 SSOT 코멘트 링크 명시로 보완.
- **공용 토큰의 플랫폼 중립성**: commonMain에서 Compose `Color` 등을 import하면 iOS 공용성 깨짐. 코드리뷰 + 빌드 검증으로 막음.
- **첫 미러링 비용**: 처음 한 번 전체 동기화가 가장 무거움. 이후엔 변경분만.

---

## 관련 자료

- [디자인 시스템 동기화 전략 플랜 (내부)](../../../../.claude/plans/figma-dev-glowing-badger.md) — 의사결정 배경 상세
- 토스 디자인 시스템 사상 참고:
  - SLASH 21 — TDS로 UI 쌓기
  - SLASH 22 — UX와 DX, 그 모든 경험을 위한 디자인 시스템
