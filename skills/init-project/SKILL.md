---
name: init-project
description: |
  새 프로젝트를 부트스트랩하는 스킬. Git/GitHub 초기화, 규칙 7개 + 스킬 14개 복사, develop 브랜치 생성, 라벨 생성, product-blueprint 생성까지 한번에 수행한다.
  이 스킬은 다음과 같은 요청에 반드시 사용한다: "새 프로젝트 시작", "프로젝트 초기화", "프로젝트 셋업", "/init-project", "프로젝트 만들어줘", "레포 생성해줘".
  새 프로젝트를 시작하거나 초기 환경을 세팅하는 맥락이면 이 스킬을 사용한다.
user_invocable: true
---

# Init Project (프로젝트 부트스트랩)

새 프로젝트를 시작할 때 필요한 모든 초기 설정을 한번에 수행하는 스킬입니다.

## 트리거 조건

다음과 같은 요청이 들어올 때 자동 발동:
- "새 프로젝트 시작", "프로젝트 초기화"
- "/init-project"
- "사이드 프로젝트 셋업해줘"

## 실행 단계

### Step 1: 프로젝트 정보 수집

**Actions:**

사용자에게 다음 정보를 물어본다:

1. **프로젝트 이름** (kebab-case, 예: `my-workout-app`)
2. **프로젝트 경로** (기본값: `~/side-project-{프로젝트이름}`)
3. **GitHub 레포 공개 여부** (`--public` 또는 `--private`, 기본값: `--private`)
4. **프로젝트 설명** (한 줄)

### Step 2: 디렉토리 및 Git 초기화

**Actions:**

```bash
# 디렉토리 생성 (또는 기존 디렉토리 확인)
mkdir -p {프로젝트경로}
cd {프로젝트경로}

# Git 초기화
git init

# GitHub 레포 생성
gh repo create {프로젝트이름} --{공개여부} --description "{설명}" --source=. --remote=origin
```

기존 디렉토리에 이미 git이 있으면 초기화를 건너뛴다.
기존 GitHub 레포가 있으면 생성을 건너뛴다.

```bash
# develop 브랜치 생성
git checkout -b develop
git push -u origin develop
git checkout main
```

### Step 3: 설정 템플릿 복사

**Actions:**

`side-project-claude-settings` 레포에서 필요한 파일을 복사한다:

```bash
# 소스 경로 (이 레포)
SOURCE=~/side-project-claude-settings

# 규칙 복사
mkdir -p {프로젝트경로}/.claude/rules
cp "$SOURCE/rules/"*.md {프로젝트경로}/.claude/rules/

# 스킬 복사 (init-project, skill-creator 제외 — 메타 레포 전용 스킬)
mkdir -p {프로젝트경로}/.claude/skills
for skill in app-plan design-system-to-figma prd-to-figma dev-plan dev-roadmap create-issues handoff resume product-blueprint interview sync-roadmap clarify sync clear-with-lessons; do
  cp -r "$SOURCE/skills/$skill" {프로젝트경로}/.claude/skills/
done

# hooks + settings 복사
mkdir -p {프로젝트경로}/hooks
cp "$SOURCE/hooks/"*.sh {프로젝트경로}/hooks/
chmod +x {프로젝트경로}/hooks/*.sh
cp "$SOURCE/.claude/settings.json" {프로젝트경로}/.claude/settings.json
```

> **참고**: 플랫폼별 전문 스킬(예: swift-concurrency, flutter-state, nextjs-patterns 등)은 `/dev-plan` 실행 시 tech stack 결정 후 자동으로 검색/설치됩니다. 이 단계에서는 범용 스킬만 복사합니다.

### Step 4: 프로젝트 구조 생성

**Actions:**

```bash
# 필수 디렉토리 생성
mkdir -p docs/ssot/prd
mkdir -p docs/ssot/design/system
mkdir -p docs/ssot/design/screens
mkdir -p docs/ssot/dev
mkdir -p docs/refs
mkdir -p docs/handoff
mkdir -p docs/lessons
mkdir -p docs/insights
mkdir -p docs/sessions

# .gitkeep 추가 (빈 디렉토리 유지)
touch docs/ssot/prd/.gitkeep
touch docs/ssot/design/system/.gitkeep
touch docs/ssot/design/screens/.gitkeep
touch docs/ssot/dev/.gitkeep
touch docs/refs/.gitkeep
touch docs/handoff/.gitkeep
touch docs/lessons/.gitkeep
touch docs/sessions/.gitkeep
```

3. **초기 product-blueprint.html 생성**

`docs/ssot/product-blueprint.html`을 아래 조건으로 생성한다:

- `<title>`에 프로젝트 이름을 포함한다
- 5개 탭 구조: **기획** | **디자인 시스템** | **화면 디자인** | **개발 계획** | **로드맵**
- 각 탭 내용에는 안내 문구를 표시한다:
  - 기획: "아직 작성되지 않음 — `/app-plan` 실행 후 자동 업데이트됩니다"
  - 디자인 시스템: "아직 작성되지 않음 — `/design-system-to-figma` 실행 후 자동 업데이트됩니다"
  - 화면 디자인: "아직 작성되지 않음 — `/prd-to-figma` 실행 후 자동 업데이트됩니다"
  - 개발 계획: "아직 작성되지 않음 — `/dev-plan` 실행 후 자동 업데이트됩니다"
  - 로드맵: "아직 작성되지 않음 — `/dev-roadmap` 실행 후 자동 업데이트됩니다"
- **standalone HTML**: 인라인 CSS만 사용, 외부 의존성 없음
- 탭 전환은 인라인 JavaScript로 구현
- URL 해시 기반 탭 네비게이션 지원 (`#기획`, `#디자인시스템` 등)

### Step 5: GitHub 라벨 생성

**Actions:**

```bash
gh label create "epic" --description "에픽 이슈" --color "6f42c1" 2>/dev/null || true
gh label create "claude-task" --description "Claude Code가 독립 수행 가능한 작업" --color "0075ca" 2>/dev/null || true
gh label create "human-task" --description "사람이 직접 수행해야 하는 작업" --color "e4e669" 2>/dev/null || true
gh label create "android" --description "안드로이드 클라이언트 관련" --color "a4c639" 2>/dev/null || true
gh label create "backend" --description "서버/API 관련" --color "d73a4a" 2>/dev/null || true
gh label create "design" --description "디자인 관련" --color "f9d0c4" 2>/dev/null || true
gh label create "infra" --description "인프라/CI/CD 관련" --color "c5def5" 2>/dev/null || true
```

### Step 6: 초기 커밋

**Actions:**

```bash
git add -A
git commit -m "Init: 프로젝트 초기 설정

- 규칙 7개 + 스킬 14개 + hooks 복사
- develop 브랜치 생성
- docs/ 디렉토리 구조 생성 (ssot/prd, ssot/design, ssot/dev, refs, handoff, lessons, sessions)
- 초기 product-blueprint.html 생성 (5개 탭 placeholder)
- GitHub 라벨 생성 (epic, claude-task, human-task, android, backend, design, infra)"

git push -u origin main
```

### Step 7: 완료 안내

**Actions:**

사용자에게 결과를 안내한다:

```
프로젝트가 초기화되었습니다!

위치: {프로젝트경로}
GitHub: https://github.com/{사용자}/{프로젝트이름}

포함된 항목:
- 규칙 7개 (ssot, workflow, history, github-issues, meta, source-citation, git-flow)
- 스킬 14개 (app-plan, design-system-to-figma, prd-to-figma, dev-plan, dev-roadmap, create-issues, handoff, resume, product-blueprint, interview, sync-roadmap, clarify, sync)
- hooks (log-conversation: 대화 기록 자동 로깅)
- Git Flow (main + develop 브랜치, PR 리뷰 필수)
- docs/ 디렉토리 구조 (ssot/prd, ssot/design, ssot/dev, refs/, handoff/, lessons/, sessions/)
- 초기 product-blueprint.html (5개 탭 placeholder)
- GitHub 라벨 (epic, claude-task, human-task, android, backend, design, infra)

다음 단계:
`/app-plan` 또는 `/interview`로 기획을 시작하세요.
이슈는 작업을 진행하면서 필요할 때 생성합니다.
```

---

## 모델 선택 가이드

- 전 과정: `sonnet` (CLI 명령 실행 + 파일 복사)

---

## 품질 체크리스트

- [ ] Git 레포가 정상 초기화되었는가?
- [ ] GitHub 레포가 생성/연결되었는가?
- [ ] 모든 규칙이 `.claude/rules/`에 복사되었는가?
- [ ] 13개 스킬이 `.claude/skills/`에 복사되었는가? (ideation 제외)
- [ ] `hooks/log-conversation.sh`가 복사되고 실행 권한이 있는가?
- [ ] `.claude/settings.json`에 UserPromptSubmit, PostToolUse hook이 설정되었는가?
- [ ] `docs/ssot/prd/`, `docs/ssot/design/system/`, `docs/ssot/design/screens/`, `docs/ssot/dev/`, `docs/refs/`, `docs/handoff/`, `docs/lessons/`, `docs/sessions/` 디렉토리가 존재하는가?
- [ ] `docs/ssot/product-blueprint.html`이 생성되었고, 5개 탭(기획/디자인 시스템/화면 디자인/개발 계획/로드맵)이 포함되어 있는가?
- [ ] `product-blueprint.html`이 standalone HTML(인라인 CSS, 외부 의존성 없음)인가?
- [ ] GitHub 라벨 7개(epic, claude-task, human-task, android, backend, design, infra)가 생성되었는가?
- [ ] 초기 커밋이 push되었는가?

---

## 관련 스킬

- `app-plan` - 기획서 작성 (다음 단계)
