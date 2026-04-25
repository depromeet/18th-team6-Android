---
name: create-sub_issue
description: |
  GitHub에 Epic 하위 작업 이슈를 생성할 때 사용하는 스킬.
  이 스킬은 다음과 같은 요청에 반드시 사용한다: "하위 이슈 만들어줘", "서브 이슈 생성", "sub issue 생성", "create-sub_issue", "/create-sub_issue".
  Epic 아래의 구체적인 단일 작업 이슈를 만들거나, 하위 작업 이슈 본문 초안을 작성하는 맥락이면 이 스킬을 사용한다.
user_invocable: true
---

# Create Sub Issue

Epic 아래에서 바로 실행 가능한 단일 작업 이슈를 생성한다.

## 기준 문서

- `.github/ISSUE_TEMPLATE/issue.yml`
- `rules/github-issues.md`

## 실행 원칙

- 하위 이슈 하나는 하나의 명확한 작업만 담는다.
- 본문에는 상위 Epic 이슈 번호 또는 URL을 반드시 포함한다.
- 작업 성격에 따라 `claude-task` 또는 `human-task` 라벨을 붙인다.
- 영향 영역에 따라 `android`, `backend`, `design`, `infra` 라벨을 붙인다.
- 모든 이슈에는 assignee를 지정한다.
- `claude-task`는 이슈 본문만 읽고 독립 수행 가능한 수준으로 작성한다.
- `human-task`는 처음 하는 사람도 따라할 수 있도록 단계별 가이드를 작성한다.
- 인터넷 최신 정보가 필요한 내용은 검색 후 출처 링크를 본문에 남긴다.

## 입력으로 받아야 할 것

- 상위 Epic 이슈 번호 또는 URL
- 이슈 제목
- 작업 내용
- 관련 문서 또는 참고 링크
- 할 일
- 완료 기준 / 검증 방법
- 작업 성격: `claude-task` 또는 `human-task`
- 영향 영역 라벨
- assignee

사용자 메시지나 Epic 본문에서 추론 가능한 값은 다시 묻지 않는다. 필수 정보가 부족하면 짧게 질문한다.

## 기본 본문 형식

```markdown
## 작업 내용

{작업 내용}

## 맥락

- 상위 Epic: #{에픽번호}
- 관련 문서:
- 전체 흐름 내 위치: {이전 작업} -> **이 작업** -> {다음 작업}

## 할 일

- [ ] {구체적 작업 1}
- [ ] {구체적 작업 2}

## 완료 기준 / 검증

- [ ] {검증 항목 1}
- [ ] {검증 항목 2}
```

## Human Task 본문 형식

사람이 외부 서비스 설정, 결제, 권한 부여, 수동 확인 등을 해야 하는 경우에는 아래 형식을 사용한다.

```markdown
## 작업 내용

{작업 내용}

## 맥락

- 상위 Epic: #{에픽번호}
- 관련 문서:
- 전체 흐름 내 위치: {이전 작업} -> **이 작업** -> {다음 작업}

## 목적

{왜 이 작업이 필요한지}

## 단계별 가이드

- [ ] Step 1: {설명} -> [서비스 링크](URL)
- [ ] Step 2: {설명}
- [ ] Step 3: {설명}

## 완료 기준 / 검증

- [ ] {확인 항목}
```

## 생성 절차

1. 상위 Epic 이슈를 확인한다.
   ```bash
   gh issue view {에픽번호}
   ```
2. 기존 라벨을 확인한다.
   ```bash
   gh label list
   ```
3. 본문을 임시 파일에 작성한다.
4. 이슈를 생성한다.
   ```bash
   gh issue create --title "{제목}" --body-file {본문파일} --label {claude-task|human-task} --label {영역라벨} --assignee {assignee}
   ```
5. 생성된 이슈 URL을 상위 Epic의 하위 작업 체크리스트에 반영할 필요가 있으면 `gh issue edit`으로 Epic 본문을 업데이트한다.

## 품질 체크리스트

- [ ] 상위 Epic 링크가 포함되었는가?
- [ ] 이슈가 하나의 작업만 담는가?
- [ ] `claude-task` 또는 `human-task` 라벨이 포함되었는가?
- [ ] 영향 영역 라벨이 포함되었는가?
- [ ] 완료 기준이 검증 가능한 문장인가?
- [ ] assignee가 지정되었는가?
