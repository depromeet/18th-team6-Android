# Git Convention

이 문서는 `rules/git-flow.md`와 `rules/github-issues.md`를 기준으로 정리한 OBRit Git 작업 규칙이다. Git 작업, 브랜치 생성, 커밋, PR, GitHub Issue 작업을 할 때 이 문서를 먼저 확인한다.

## 기본 원칙

- `main`과 `develop`에는 direct push 하지 않는다.
- 모든 변경은 PR을 통해 병합한다.
- PR은 최소 1명의 리뷰 승인 후 병합한다.
- 작업 시작 시 `develop`에서 작업 브랜치를 만든다.
- 작업 완료 시 `develop`으로 PR을 생성한다.
- `develop`에서 `main`으로의 병합은 릴리스 시점에만 수행한다.
- 사용자가 명시적으로 요청하지 않는 한 branch, commit, tag, push, PR을 만들지 않는다.

## 브랜치 전략

| 브랜치 | 용도 | 병합 대상 |
| --- | --- | --- |
| `main` | 프로덕션 배포 | 없음 |
| `develop` | 개발 통합 및 검증 | `main` |
| `feat/{이슈번호}-{설명}` | 기능 개발 | `develop` |
| `fix/{이슈번호}-{설명}` | 버그 수정 | `develop` |

## 브랜치 이름

- 이슈 번호를 접두사로 포함한다.
- 이슈 이름과 브랜치 이름은 가능한 동일하게 유지한다.
- 설명은 kebab-case로 작성한다.
- 작업 내용을 짧고 명확하게 표현한다.

```text
feat/#1-home-ui
fix/#57-crash-on-startup
```

## 커밋 규칙

커밋 메시지는 Conventional Commits를 따른다.

```text
<type>: <summary>
```

자주 쓰는 type:

- `feat`: 사용자에게 보이는 기능 추가
- `fix`: 버그 수정
- `docs`: 문서 변경
- `refactor`: 동작 변경 없는 구조 개선
- `test`: 테스트 추가 또는 수정
- `chore`: 빌드/설정/정리 작업
- `build`: Gradle, dependency, build logic 변경
- `ci`: CI/CD 설정 변경

예시:

```text
feat: agent 목록 화면 상태 처리 추가
fix: 빈 응답에서 세션 로딩 실패 처리
docs: git convention 문서 추가
build: compose compiler 설정 정리
```

## 커밋 작성 원칙

- 한 커밋은 하나의 명확한 작업만 담는다.
- 관련 없는 파일을 함께 stage하지 않는다.
- 커밋 전 `git status --short`로 변경 범위를 확인한다.
- 가능하면 커밋 전에 관련 검증 명령을 실행한다.
- 검증을 실행하지 못했다면 PR 설명이나 작업 보고에 이유를 남긴다.
- secret, local path, `local.properties` 같은 local-only 정보를 커밋하지 않는다.

## PR 규칙

- PR 대상 브랜치는 기본적으로 `develop`이다.
- PR 본문에는 작업 요약, 검증 방법, 관련 이슈를 포함한다.
- UI 또는 동작 변화가 있으면 확인 방법을 구체적으로 적는다.
- 리뷰에서 수정 요청을 받으면 관련 커밋을 추가하고, 불필요한 변경은 섞지 않는다.

## GitHub Issue 운영

- 진행 상황은 GitHub Projects와 Issues로 관리한다.
- 처음부터 이슈를 일괄 생성하지 않고, 작업을 진행하면서 필요할 때 생성한다.
- 기능 개발 시작 시점에 이슈를 만든다.
- 이슈 하나에는 하나의 명확한 작업만 담는다.
- 이슈 생성 전 사용자와 범위와 분류를 합의한다.

## Issue 분류

- 에픽 이슈는 큰 단위의 마일스톤이며 `epic` 라벨을 붙인다.
- 하위 작업 이슈는 에픽 아래의 구체적 작업이며 에픽 이슈 링크를 본문에 포함한다.
- 모든 이슈에는 담당자를 지정한다.
- 주요 라벨:
  - `claude-task`: AI agent가 독립적으로 수행 가능한 작업
  - `human-task`: 사람이 직접 수행해야 하는 작업
  - `epic`: 에픽 이슈
  - `android`: Android client 관련
  - `backend`: server/API 관련
  - `design`: design 관련
  - `infra`: infra/CI/CD 관련

## Agent 주의사항

- 사용자가 요청한 Git 작업만 수행한다.
- 현재 작업과 무관한 사용자 변경분은 stage하지 않는다.
- dirty worktree에서는 내가 수정한 파일과 기존 변경분을 구분한다.
- `git reset --hard`, `git checkout --`, 강제 push 같은 파괴적 명령은 사용자가 명시적으로 요청한 경우에만 수행한다.
- PR, issue, push 등 원격 작업에는 `gh` CLI를 사용하되, 필요한 권한이나 네트워크 문제가 있으면 사용자에게 알린다.

