# GitHub Rulesets

이 디렉토리에는 GitHub Ruleset JSON 파일을 보관합니다. GitHub UI에서 import하여 적용합니다.

## 적용 방법

1. https://github.com/depromeet/18th-team6-Android/settings/rules 접속
2. **New ruleset** → **Import a ruleset** 클릭
3. 이 디렉토리의 JSON 파일 업로드
4. 내용 확인 후 **Create** 클릭

## 파일 목록

### `protect-main-develop.json`

`main`, `develop` 브랜치 보호 규칙.

- **deletion 차단**: 브랜치 삭제 불가
- **non_fast_forward 차단**: force push 불가
- **pull_request 필수**:
  - 승인 1명 필요
  - 새 커밋 푸시 시 기존 승인 무효화 (dismiss_stale_reviews_on_push)
  - 모든 대화 resolved 상태여야 머지 가능 (required_review_thread_resolution)
  - 허용 머지 방식: merge / squash / rebase

> **주의**: Ruleset은 Free 플랜 private 레포에서는 적용되지 않을 수 있습니다. 403 에러가 나면 레포를 public으로 전환하거나 GitHub Team 이상 플랜으로 업그레이드가 필요합니다.

## 참고

- [GitHub Docs - Creating rulesets](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-rulesets/creating-rulesets-for-a-repository)
- [GitHub Docs - Available rules](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-rulesets/available-rules-for-rulesets)
