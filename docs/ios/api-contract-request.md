# iOS API Contract Requests

작성일: 2026-06-09

확인 문서:

- Swagger UI: https://orbit-dep.site/swagger-ui/index.html
- OpenAPI JSON: https://orbit-dep.site/v3/api-docs

## 1. 홈 리스트 메타데이터 추가

### 현재 상황

`GET /home/items` 응답은 현재 페이지 데이터만 제공한다.

```json
{
  "content": [],
  "nextCursor": 1001,
  "size": 20,
  "hasNext": true
}
```

전체 아이템 개수와 필터 범위가 없다.

### 원인

iOS 리스트 화면은 전체 개수와 필터 슬라이더 범위가 필요하다.

현재 응답에는 해당 정보가 없어 iOS에서 아래처럼 임시 계산 중이다.

```swift
totalItemCount = currentPageItems.count + (hasNext ? 1 : 0)
filterBounds = currentPageItems min/max
```

이 방식은 실제 데이터가 100개여도 첫 페이지 20개 기준으로 전체 개수와 필터 범위를 잘못 표시할 수 있다.

### 요청사항

`GET /home/items` 응답에 아래 필드를 추가한다.

```json
{
  "content": [],
  "nextCursor": 1001,
  "size": 20,
  "hasNext": true,
  "totalCount": 137,
  "filterBounds": {
    "minDday": -12,
    "maxDday": 90,
    "minSpareQuantity": 0,
    "maxSpareQuantity": 12
  }
}
```

필드 정의:

- `totalCount`: 현재 요청 필터가 적용된 전체 결과 개수
- `filterBounds.minDday`: 필터 UI에서 사용할 최소 D-day
- `filterBounds.maxDday`: 필터 UI에서 사용할 최대 D-day
- `filterBounds.minSpareQuantity`: 필터 UI에서 사용할 최소 여분 수량
- `filterBounds.maxSpareQuantity`: 필터 UI에서 사용할 최대 여분 수량

`filterBounds`는 가능하면 필터 적용 전 사용자의 전체 활성 소모품 기준으로 내려준다.

### 기대효과

iOS가 전체 페이지를 prefetch하지 않아도 정확한 전체 개수와 필터 범위를 표시할 수 있다.

불필요한 반복 호출 없이 첫 페이지 응답만으로 리스트 화면을 구성할 수 있다.

## 2. 아이템 대표 이미지 수정 지원

### 현재 상황

`PATCH /items/{itemId}` 요청 스키마는 현재 아래 필드만 지원한다.

```json
{
  "name": "욕실 칫솔",
  "spareQuantity": 2,
  "lastReplacedDate": "2026-04-18",
  "replacementIntervalDays": 30
}
```

대표 이미지 변경용 `iconId` 또는 `iconUrl` 필드가 없다.

`POST /categories`는 `iconId`를 받지만, `PATCH /categories/{categoryId}`는 없다. 현재 `/categories/{categoryId}`는 삭제만 지원한다.

### 원인

iOS 상세 편집 화면에는 대표 이미지 선택 UI가 있다.

하지만 서버에 대표 이미지 변경을 저장할 API 필드가 없어, 현재는 앱 로컬 상태에만 변경값을 반영할 수 있다.

서버 데이터를 다시 조회하면 이미지 변경이 유지되지 않는다.

### 요청사항

아이템별 대표 이미지 수정을 지원한다.

`PATCH /items/{itemId}` 요청에 `iconId`를 추가한다.

```json
{
  "name": "욕실 칫솔",
  "spareQuantity": 2,
  "lastReplacedDate": "2026-04-18",
  "replacementIntervalDays": 30,
  "iconId": 3
}
```

응답에도 이미지 식별 정보를 포함한다.

- `ItemResponse`: `iconId` 또는 `iconUrl` 추가
- `ItemDetailResponse`: 기존 `iconUrl`이 변경된 대표 이미지를 반영해야 함
- `HomeItemCard`: 기존 `iconUrl`이 변경된 대표 이미지를 반영해야 함

### 기대효과

iOS 상세 편집에서 선택한 대표 이미지가 서버에 저장된다.

편집 완료 후 홈, 리스트, 상세 화면을 다시 조회해도 동일한 이미지가 유지된다.

