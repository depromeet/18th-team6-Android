# Git Flow

## 브랜치 전략

| 브랜치 | 용도 | 병합 대상 |
|--------|------|-----------|
| `main` | 프로덕션 배포 | — |
| `develop` | 개발 통합 (검증) | `main` |
| `feat/{이슈번호}-{설명}` | 기능 개발 | `develop` |
| `fix/{이슈번호}-{설명}` | 버그 수정 | `develop` |

## 규칙

- `main`은 direct push 금지. PR을 통해서만 병합
- `develop`도 direct push 금지. PR을 통해서만 병합
- **PR은 최소 1명의 리뷰 승인 후 병합한다**
- 작업 시작 시 `develop`에서 브랜치를 생성한다
- 작업 완료 시 `develop`으로 PR을 생성한다
- `develop` → `main` 병합은 릴리스 시점에 수행한다

## 브랜치 이름 규칙

```
feat/1-홈-UI-생성
fix/57-crash-on-startup
```

- **이슈 번호를 접두사로 포함**
- 이슈 이름과 브랜치 이름을 동일하게 유지
- kebab-case 사용
- 간결하게 작업 내용을 설명

## 커밋 규칙

Conventional Commits를 따른다. CLAUDE.md의 커밋 컨벤션 섹션 참조.
