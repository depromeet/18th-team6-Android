package com.obrit.feature.widget

import com.obrit.obrit.shared.data.repository.HomeRepository
import com.obrit.obrit.shared.model.home.HomeBucketGroup
import com.obrit.obrit.shared.model.home.HomeBucketType
import com.obrit.obrit.shared.model.home.MyStatusSummary
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * 위젯에 필요한 값을 홈 API 두 개에서 병렬로 읽어온다.
 *
 * - 양호 비율: `GET /home/my-summary`의 totalCount, needReplaceCount
 * - 위험/경고 건수: `GET /home/buckets`의 bucket별 count
 *
 * needReplaceCount와 DANGER+WARNING count는 서버에서 별개로 계산되므로 각각 호출한다.
 *
 * @return 둘 중 하나라도 실패하면 null
 */
internal suspend fun loadStatusWidgetState(homeRepository: HomeRepository): StatusWidgetState? =
    coroutineScope {
        val summaryDeferred = async { homeRepository.getMyStatusSummary() }
        val bucketsDeferred = async { homeRepository.getBuckets() }

        val summary = summaryDeferred.await().getOrNull() ?: return@coroutineScope null
        val buckets = bucketsDeferred.await().getOrNull() ?: return@coroutineScope null

        StatusWidgetState(
            dangerCount = buckets.countOf(HomeBucketType.DANGER),
            warningCount = buckets.countOf(HomeBucketType.WARNING),
            goodRatio = summary.goodRatio(),
        )
    }

/** 홈 화면과 같은 식으로 계산한다. HomeScreenSuccessContent의 negativeRatio와 짝을 이룬다. */
private fun MyStatusSummary.goodRatio(): Float {
    val total = totalCount.coerceAtLeast(1)
    return 1f - needReplaceCount.toFloat() / total
}

private fun List<HomeBucketGroup>.countOf(bucket: HomeBucketType): Int = firstOrNull { group -> group.bucket == bucket }?.count ?: 0
