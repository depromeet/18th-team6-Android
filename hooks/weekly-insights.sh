#!/bin/bash
# 주간 인사이트 정리 스크립트
# docs/lessons/ 의 최근 1주일 교훈을 취합하여 docs/insights/insights.md에 추가
# 사용법: bash hooks/weekly-insights.sh
# cron 예시: 0 9 * * 1 cd /path/to/project && bash hooks/weekly-insights.sh

LESSONS_DIR="docs/lessons"
INSIGHTS_DIR="docs/insights"
INSIGHTS_FILE="${INSIGHTS_DIR}/insights.md"
TODAY=$(date +%Y-%m-%d)
ONE_WEEK_AGO=$(date -v-7d +%Y-%m-%d 2>/dev/null || date -d "7 days ago" +%Y-%m-%d)

# 디렉토리 생성
mkdir -p "$INSIGHTS_DIR"

# insights.md 헤더가 없으면 생성
if [ ! -f "$INSIGHTS_FILE" ]; then
  echo "# Insights" > "$INSIGHTS_FILE"
  echo "" >> "$INSIGHTS_FILE"
  echo "주간 단위로 정리된 교훈 모음. \`docs/lessons/\`에서 자동 취합." >> "$INSIGHTS_FILE"
  echo "" >> "$INSIGHTS_FILE"
fi

# 최근 1주일 내 수정된 lesson 파일 수집
RECENT_FILES=$(find "$LESSONS_DIR" -name "*.md" -newer "$INSIGHTS_FILE" -o -name "*.md" -newermt "$ONE_WEEK_AGO" 2>/dev/null | sort)

if [ -z "$RECENT_FILES" ]; then
  echo "최근 1주일 내 새 교훈이 없습니다."
  exit 0
fi

# 주간 섹션 추가
echo "" >> "$INSIGHTS_FILE"
echo "---" >> "$INSIGHTS_FILE"
echo "" >> "$INSIGHTS_FILE"
echo "## 주간 정리: ${ONE_WEEK_AGO} ~ ${TODAY}" >> "$INSIGHTS_FILE"
echo "" >> "$INSIGHTS_FILE"

for file in $RECENT_FILES; do
  TITLE=$(head -1 "$file" | sed 's/^#* *//')
  echo "### ${TITLE}" >> "$INSIGHTS_FILE"
  echo "" >> "$INSIGHTS_FILE"
  # 첫 줄(제목) 제외한 본문 추가
  tail -n +2 "$file" >> "$INSIGHTS_FILE"
  echo "" >> "$INSIGHTS_FILE"
done

echo "✓ ${TODAY}: $(echo "$RECENT_FILES" | wc -l | tr -d ' ')개 교훈이 insights.md에 추가되었습니다."
