# Android Team Claude Settings

안드로이드 팀 프로젝트를 위한 Claude Code 규칙 + 스킬 모음.

## 팀 합의 사항

### 프로젝트 관리
- **GitHub Projects** 기반으로 이슈 발행 및 진행 상황 관리
- 처음부터 이슈를 일괄 생성하지 않는다. 작업을 진행하면서 필요할 때 이슈를 생성한다
- 전체 로드맵에서 Claude가 할 수 있는 일과 사람이 해야 하는 일을 분리한다
- 기획서가 없으므로 **Issue를 보고 기능을 파악할 수 있도록** 작성한다

### PR/커밋 방식
- 처음 2주는 직접 PR 날리고 검수하는 과정을 거친다
- 2주간의 과정에서 best practice를 도출한 뒤, 토론 후 코드 리뷰 스킬을 생성한다
- 그 이후는 **커밋 전 항상 리뷰 스킬을 실행**하고 올린다
- convention, skill, best practice에 대한 리뷰를 진행한다

### 교훈 관리
- `/clear-with-lessons` 스킬로 비자명한 인사이트 저장
- `docs/lessons/` — 세션 중 발생한 교훈을 즉시 기록 
- 다른 세션에서 같은 문제를 해결하지 못할 때 insights 참고

---

## 커밋 메시지 컨벤션 (Conventional Commits)

```
<type>(<scope>): <description>
```

### 타입

| 타입 | 용도 | 예시 |
|------|------|------|
| **feat** | 새 기능 | `feat: 로그인 화면 UI 구현` |
| **fix** | 버그 수정 | `fix: 체크리스트 업데이트 API 오류 수정` |
| **docs** | 문서 변경 | `docs: README 설치 가이드 추가` |
| **style** | 포맷팅, 세미콜론 등 (로직 변경 없음) | `style: ktlint 포맷 적용` |
| **refactor** | 리팩토링 (기능/버그 변경 없음) | `refactor: Repository 패턴 적용` |
| **perf** | 성능 개선 | `perf: 이미지 로딩 캐시 최적화` |
| **test** | 테스트 추가/수정 | `test: LoginViewModel 단위 테스트 추가` |
| **build** | 빌드 시스템, 의존성 변경 | `build: Kotlin 버전 2.0으로 업데이트` |
| **ci** | CI 설정 변경 | `ci: GitHub Actions 워크플로우 추가` |
| **chore** | 기타 (src/test 미변경) | `chore: .gitignore 업데이트` |
| **revert** | 커밋 되돌리기 | `revert: feat: 로그인 화면 UI 구현` |

### Scope (선택)

변경 영역을 괄호로 표시한다. 팀 내 모듈명 또는 기능명을 사용한다.
```
feat(auth): OAuth2 로그인 추가
fix(home): 피드 목록 스크롤 버그 수정
```

### 규칙
- 본인이 작업한 내용을 이해한 상태에서 커밋한다
- 무지성 커밋 금지
- 한글 설명 사용

---

## 브랜치 컨벤션

| Prefix | 용도 | 예시 |
|--------|------|------|
| `feat/` | 새 기능 개발 | `feat/1-홈-UI-생성` |
| `fix/` | 버그 수정 | `fix/57-crash-on-startup` |
| `chore/` | 설정, 잡무 | `chore/12-gradle-업데이트` |
| `refactor/` | 리팩토링 | `refactor/20-repository-패턴-적용` |
| `docs/` | 문서 작업 | `docs/8-README-작성` |

### 규칙
- **이슈 번호를 반드시 포함**한다
- 이슈 이름과 브랜치 이름을 동일하게 유지한다
- kebab-case 사용

---

## 디자인

- 디자이너가 별도로 있으므로 Figma 관련 스킬(`/design-system-to-figma`, `/prd-to-figma`)은 선택사항이다
- 디자이너의 Figma 결과물을 참고하여 개발한다
