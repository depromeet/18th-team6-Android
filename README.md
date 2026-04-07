# 디프만 18기 Android 6팀

Claude Code 규칙 + 스킬 모음. 프로젝트 시작 시 `/init-project`로 새 레포에 복사하여 사용.

## 구성

| 디렉토리 | 내용 |
|----------|------|
| `rules/` | Claude 행동 규칙 7개 (git-flow, workflow, ssot 등) |
| `skills/` | 스킬 14개 (기획, 개발 계획, 세션 관리 등) |
| `hooks/` | 대화 로깅, 주간 인사이트 정리 |
| `CLAUDE.md` | Claude 전용 지시사항 |

## 팀 합의사항

### 프로젝트 관리

- **GitHub Projects** 기반으로 이슈 발행 및 진행 상황 관리
- 처음부터 이슈를 일괄 생성하지 않고, 진행하면서 필요할 때 생성
- 기획서가 없으므로 **Issue를 보고 기능을 파악할 수 있도록** 작성

### PR/커밋 방식

- 처음 2주는 직접 PR 날리고 검수하는 과정을 거침
- 2주간의 과정에서 best practice 도출 → 토론 후 코드 리뷰 스킬 생성
- 이후 **커밋 전 항상 리뷰 스킬 실행** 후 올리기

### 커밋 메시지 컨벤션 (Conventional Commits)

```
<type>(<scope>): <description>
```

| 타입 | 용도 | 예시 |
|------|------|------|
| **feat** | 새 기능 | `feat: 로그인 화면 UI 구현` |
| **fix** | 버그 수정 | `fix: 체크리스트 업데이트 API 오류 수정` |
| **docs** | 문서 변경 | `docs: README 설치 가이드 추가` |
| **style** | 포맷팅 (로직 변경 없음) | `style: ktlint 포맷 적용` |
| **refactor** | 리팩토링 | `refactor: Repository 패턴 적용` |
| **perf** | 성능 개선 | `perf: 이미지 로딩 캐시 최적화` |
| **test** | 테스트 추가/수정 | `test: LoginViewModel 단위 테스트 추가` |
| **build** | 빌드/의존성 변경 | `build: Kotlin 버전 2.0으로 업데이트` |
| **ci** | CI 설정 변경 | `ci: GitHub Actions 워크플로우 추가` |
| **chore** | 기타 | `chore: .gitignore 업데이트` |

- Scope는 선택: `feat(auth): OAuth2 로그인 추가`
- 본인이 작업한 내용을 이해한 상태에서 커밋 (무지성 커밋 금지)

### 브랜치 전략

`main` (배포) → `develop` (검증) → 개별 feature

| Prefix | 용도 | 예시 |
|--------|------|------|
| `feat/` | 새 기능 | `feat/1-홈-UI-생성` |
| `fix/` | 버그 수정 | `fix/57-crash-on-startup` |
| `chore/` | 설정/잡무 | `chore/12-gradle-업데이트` |
| `refactor/` | 리팩토링 | `refactor/20-repository-패턴-적용` |
| `docs/` | 문서 | `docs/8-README-작성` |

- 이슈 번호 필수 포함, 이슈 이름 = 브랜치 이름
- PR은 최소 1명 리뷰 승인 후 병합

## 사용법

```
> /init-project
```
