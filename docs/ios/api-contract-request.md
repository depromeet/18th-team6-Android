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

## 3. 교체 완료 API의 여분 수량 차감 보장

### 현재 상황

`POST /items/{itemId}/replacements` 호출 후 응답의 `spareQuantity`가 기존 수량 그대로 내려오는 경우가 있다.

예를 들어 교체 완료 전 여분 수량이 3개인 소모품을 교체 완료 처리하면, 기대 응답은 `spareQuantity: 2`지만 현재 응답이 `spareQuantity: 3`으로 유지될 수 있다.

```json
{
  "itemId": 1,
  "categoryId": 10,
  "categoryName": "욕실",
  "name": "칫솔",
  "spareQuantity": 2,
  "replacementIntervalDays": 30,
  "lastReplacedDate": "2026-06-19",
  "nextReplacementDate": "2026-07-19"
}
```

### 원인

Android 상세 화면은 `itemRepository.createReplacement()`가 반환한 `Item.count`를 그대로 상세 상태의 여분 수량으로 사용한다.

iOS 상세 화면도 `SharedWriteService.createReplacement()`가 반환한 `Item.count`를 그대로 상세 상태의 여분 수량으로 사용해야 한다.

따라서 교체 완료에 따른 여분 수량 차감은 클라이언트가 별도 `PATCH /items/{itemId}/spare-count`를 호출해서 맞추는 것이 아니라, 서버가 교체 완료 처리와 함께 보장해야 한다.

### 요청사항

`POST /items/{itemId}/replacements`에서 교체 기록 생성과 여분 수량 차감을 하나의 서버 트랜잭션으로 처리한다.

- 교체 기록을 생성한다.
- `lastReplacedDate`를 요청의 `replacedDate`로 갱신한다.
- 현재 `spareQuantity`가 1 이상이면 `spareQuantity - 1`로 갱신한다.
- 현재 `spareQuantity`가 0이면 0으로 유지한다.
- 응답 `ItemResponse.spareQuantity`는 차감이 반영된 최신 값을 내려준다.
- 이후 `GET /items`, `GET /home/items`, `GET /items/{itemId}/replacements`와 관련 홈/상세 데이터도 같은 서버 상태를 반영한다.

교체 기록 생성은 성공했지만 여분 수량 차감은 실패하는 부분 성공 상태가 생기지 않도록 원자적으로 처리되어야 한다.

### 기대효과

Android와 iOS가 동일하게 교체 완료 API 응답만으로 화면 상태를 갱신할 수 있다.

클라이언트가 교체 완료 후 별도 여분 수량 수정 API를 호출하지 않아도 되어, 네트워크 실패나 동시 요청으로 교체 기록과 여분 수량이 어긋나는 상황을 줄일 수 있다.
