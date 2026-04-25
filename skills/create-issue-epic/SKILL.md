---
name: create-issue-epic
description: |
  GitHub에 Epic 이슈를 생성할 때 사용하는 스킬.
  이 스킬은 다음과 같은 요청에 반드시 사용한다: "Epic 이슈 만들어줘", "에픽 이슈 생성", "create-issue-epic", "/create-issue-epic".
  여러 하위 작업을 묶는 큰 단위의 마일스톤을 GitHub Issue로 만들거나, Epic 본문 초안을 작성하는 맥락이면 이 스킬을 사용한다.
user_invocable: true
---

# Create Issue Epic

GitHub Projects + Issues 기반 작업 관리 원칙에 맞춰 Epic 이슈를 생성한다.

## 기준 문서

- `.github/ISSUE_TEMPLATE/epic.yml`
- `rules/github-issues.md`

## 실행 원칙

- Epic은 여러 하위 작업을 묶는 큰 단위의 마일스톤이다.
- GitHub 이슈에는 반드시 `epic` 라벨을 붙인다.
- 가능하면 `android`, `backend`, `design`, `infra` 등 영향 영역 라벨도 함께 붙인다.
- 모든 이슈에는 assignee를 지정한다. 사용자가 지정하지 않으면 현재 작업자 또는 사용자에게 확인한다.
- 이슈 생성 전 범위가 애매하면 먼저 범위와 제외 항목을 정리한다.
- 인터넷 최신 정보가 필요한 내용(외부 서비스 설정, 정책, SDK 최신 동작 등)이 포함되면 검색 후 출처 링크를 본문에 남긴다.

## 입력으로 받아야 할 것

- Epic 제목
- 작업 요약
- 배경 / 변경 이유
- 포함 범위
- 제외 범위
- 하위 작업 목록
- 완료 기준
- 영향 범위
- 테스트 / 검증 계획
- assignee
- 관련 문서, 디자인, API 명세, 논의 링크

사용자 메시지나 로컬 문서에서 추론 가능한 값은 다시 묻지 않는다. 필수 정보가 부족하면 짧게 질문한다.

## 본문 형식

GitHub Issue Forms YAML을 그대로 붙이지 말고, 아래 Markdown 본문으로 변환해서 생성한다.

```markdown
## 작업 요약

{요약}

## 배경 / 변경 이유

- {배경}

## 작업 범위

### 포함

- {포함 범위}

### 제외

- {제외 범위}

## 하위 작업

- [ ] {하위 작업 1}
- [ ] {하위 작업 2}
- [ ] {하위 작업 3}

## 완료 기준

- [ ] {완료 기준 1}
- [ ] {완료 기준 2}
- [ ] {완료 기준 3}

## 영향 범위

- [ ] 일반 기능 변경
- [ ] UI 변경
- [ ] API 연동 변경
- [ ] 공통 컴포넌트 변경
- [ ] 시스템 영향 가능 (`lint`, `gradle`, `manifest`, build config 등)

## 테스트 / 검증 계획

- [ ] build 통과
- [ ] ktlint 통과
- [ ] detekt 통과
- [ ] unit test 통과
- [ ] 수동 테스트 완료

## 참고 자료

- 디자인:
- API 명세:
- 관련 논의:

## 기타 메모

-
```

## 생성 절차

1. 기존 라벨을 확인한다.
   ```bash
   gh label list
   ```
2. 필요한 라벨이 없으면 사용자에게 확인 후 생성한다.
   ```bash
   gh label create epic --description "큰 단위의 마일스톤" --color 7057ff
   ```
3. 본문을 임시 파일에 작성한다.
4. 이슈를 생성한다.
   ```bash
   gh issue create --title "[Epic] {제목}" --body-file {본문파일} --label epic --assignee {assignee}
   ```
5. 생성된 이슈 URL과 하위 이슈로 분리할 후보를 보고한다.

## 품질 체크리스트

- [ ] `epic` 라벨이 포함되었는가?
- [ ] 포함/제외 범위가 구분되었는가?
- [ ] 하위 작업이 체크리스트로 정리되었는가?
- [ ] 완료 기준이 검증 가능한 문장인가?
- [ ] 관련 문서와 출처가 있으면 링크로 남겼는가?
- [ ] assignee가 지정되었는가?
