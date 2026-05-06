@file:Suppress("LongParameterList", "MagicNumber")

package com.obrit.feature.agent.viewmodel

import androidx.compose.runtime.Immutable

internal object ConsumableMemoryStore {
    val urgentConsumables: List<HomeConsumableUiModel> =
        listOf(
            HomeConsumableUiModel(
                id = 1,
                title = "프리필터",
                remainLabel = "0개 남음",
                statusFilter = HomeStatusFilter.ReplacementDanger,
                replacementLabel = "D-day",
                spareLabel = "여분 0개",
                isPrimary = true,
            ),
            HomeConsumableUiModel(
                id = 2,
                title = "활성탄 필터",
                remainLabel = "0개 남음",
                statusFilter = HomeStatusFilter.SpareShortage,
                replacementLabel = "D-3",
                spareLabel = "여분 0개",
            ),
            HomeConsumableUiModel(
                id = 3,
                title = "HEPA 필터",
                remainLabel = "1개 남음",
                statusFilter = HomeStatusFilter.ReplacementWarning,
                replacementLabel = "D+1",
                spareLabel = "여분 1개",
            ),
            HomeConsumableUiModel(
                id = 4,
                title = "더스트백",
                remainLabel = "0개 남음",
                statusFilter = HomeStatusFilter.ReplacementDanger,
                replacementLabel = "D-1",
                spareLabel = "여분 0개",
            ),
            HomeConsumableUiModel(
                id = 5,
                title = "롤러 브러시",
                remainLabel = "2개 남음",
                statusFilter = HomeStatusFilter.ReplacementWarning,
                replacementLabel = "D+2",
                spareLabel = "여분 2개",
            ),
            HomeConsumableUiModel(
                id = 6,
                title = "배터리 팩",
                remainLabel = "0개 남음",
                statusFilter = HomeStatusFilter.SpareShortage,
                replacementLabel = "D-9",
                spareLabel = "여분 0개",
            ),
            HomeConsumableUiModel(
                id = 7,
                title = "흡입 호스",
                remainLabel = "1개 남음",
                statusFilter = HomeStatusFilter.ReplacementDanger,
                replacementLabel = "D-day",
                spareLabel = "여분 1개",
            ),
            HomeConsumableUiModel(
                id = 8,
                title = "분사 노즐",
                remainLabel = "0개 남음",
                statusFilter = HomeStatusFilter.SpareShortage,
                replacementLabel = "D-6",
                spareLabel = "여분 0개",
            ),
            HomeConsumableUiModel(
                id = 9,
                title = "UV 램프",
                remainLabel = "1개 남음",
                statusFilter = HomeStatusFilter.ReplacementWarning,
                replacementLabel = "D+4",
                spareLabel = "여분 1개",
            ),
            HomeConsumableUiModel(
                id = 10,
                title = "물탱크 패킹",
                remainLabel = "1개 남음",
                statusFilter = HomeStatusFilter.ReplacementDanger,
                replacementLabel = "D+3",
                spareLabel = "여분 1개",
            ),
            HomeConsumableUiModel(
                id = 11,
                title = "극세사 패드",
                remainLabel = "2개 남음",
                statusFilter = HomeStatusFilter.ReplacementWarning,
                replacementLabel = "D+5",
                spareLabel = "여분 2개",
            ),
        )

    val previewConsumables: List<HomeConsumableUiModel> =
        listOf(
            HomeConsumableUiModel(
                id = 1,
                title = "프리필터",
                remainLabel = "82일째 사용중",
                statusFilter = HomeStatusFilter.ReplacementDanger,
                replacementLabel = "교체 D+2",
                spareLabel = "여분 0개",
                isPrimary = true,
            ),
            HomeConsumableUiModel(
                id = 2,
                title = "활성탄 필터",
                remainLabel = "76일째 사용중",
                statusFilter = HomeStatusFilter.SpareShortage,
                replacementLabel = "교체 D-3",
                spareLabel = "여분 0개",
            ),
            HomeConsumableUiModel(
                id = 3,
                title = "HEPA 필터",
                remainLabel = "71일째 사용중",
                statusFilter = HomeStatusFilter.ReplacementWarning,
                replacementLabel = "교체 D+1",
                spareLabel = "여분 1개",
            ),
            HomeConsumableUiModel(
                id = 4,
                title = "더스트백",
                remainLabel = "67일째 사용중",
                statusFilter = HomeStatusFilter.ReplacementDanger,
                replacementLabel = "교체 D+3",
                spareLabel = "여분 1개",
            ),
            HomeConsumableUiModel(
                id = 5,
                title = "롤러 브러시",
                remainLabel = "61일째 사용중",
                statusFilter = HomeStatusFilter.ReplacementWarning,
                replacementLabel = "교체 D-5",
                spareLabel = "여분 2개",
            ),
            HomeConsumableUiModel(
                id = 6,
                title = "배터리 팩",
                remainLabel = "27일째 사용중",
                statusFilter = HomeStatusFilter.SpareShortage,
                replacementLabel = "교체 D-9",
                spareLabel = "여분 0개",
            ),
        )

    val usageItems: List<HomeUsageUiModel> =
        listOf(
            HomeUsageUiModel(id = 1, title = "프리필터", daysInUse = 82),
            HomeUsageUiModel(id = 2, title = "활성탄 필터", daysInUse = 76),
            HomeUsageUiModel(id = 3, title = "HEPA 필터", daysInUse = 71),
            HomeUsageUiModel(id = 4, title = "더스트백", daysInUse = 67),
            HomeUsageUiModel(id = 5, title = "롤러 브러시", daysInUse = 61),
            HomeUsageUiModel(id = 6, title = "흡입 호스", daysInUse = 55),
            HomeUsageUiModel(id = 7, title = "분사 노즐", daysInUse = 48),
            HomeUsageUiModel(id = 8, title = "물탱크 패킹", daysInUse = 42),
            HomeUsageUiModel(id = 9, title = "UV 램프", daysInUse = 39),
            HomeUsageUiModel(id = 10, title = "청소 브러시", daysInUse = 31),
            HomeUsageUiModel(id = 11, title = "배터리 팩", daysInUse = 27),
            HomeUsageUiModel(id = 12, title = "실리콘 마개", daysInUse = 23),
            HomeUsageUiModel(id = 13, title = "극세사 패드", daysInUse = 18),
            HomeUsageUiModel(id = 14, title = "충전 케이블", daysInUse = 12),
            HomeUsageUiModel(id = 15, title = "보관 파우치", daysInUse = 7),
            HomeUsageUiModel(id = 16, title = "여행용 케이스", daysInUse = 4),
        )

    private val detailItems =
        mutableMapOf(
            1 to
                detail(
                    id = 1,
                    title = "프리필터",
                    status = ConsumableDetailStatus.Warning,
                    recentReplacementDate = "2월 13일",
                    nextReplacementDate = "5월 4일",
                    replacementBadge = "D+2",
                    spareCount = 0,
                    averageCycleDays = 34,
                    recommendedCycleDays = 30,
                    currentUsageDays = 82,
                    historyDays = listOf(29, 31, 37, 43, 82),
                ),
            2 to
                detail(
                    id = 2,
                    title = "활성탄 필터",
                    status = ConsumableDetailStatus.Healthy,
                    recentReplacementDate = "2월 20일",
                    nextReplacementDate = "5월 9일",
                    replacementBadge = "D-3",
                    spareCount = 0,
                    averageCycleDays = 32,
                    recommendedCycleDays = 35,
                    currentUsageDays = 76,
                    historyDays = listOf(28, 33, 31, 35, 76),
                ),
            3 to
                detail(
                    id = 3,
                    title = "HEPA 필터",
                    status = ConsumableDetailStatus.Warning,
                    recentReplacementDate = "2월 25일",
                    nextReplacementDate = "5월 5일",
                    replacementBadge = "D+1",
                    spareCount = 1,
                    averageCycleDays = 38,
                    recommendedCycleDays = 45,
                    currentUsageDays = 71,
                    historyDays = listOf(35, 39, 41, 36, 71),
                ),
            4 to
                detail(
                    id = 4,
                    title = "더스트백",
                    status = ConsumableDetailStatus.Warning,
                    recentReplacementDate = "2월 28일",
                    nextReplacementDate = "5월 3일",
                    replacementBadge = "D+3",
                    spareCount = 1,
                    averageCycleDays = 29,
                    recommendedCycleDays = 30,
                    currentUsageDays = 67,
                    historyDays = listOf(26, 30, 28, 33, 67),
                ),
            5 to
                detail(
                    id = 5,
                    title = "롤러 브러시",
                    status = ConsumableDetailStatus.Healthy,
                    recentReplacementDate = "3월 6일",
                    nextReplacementDate = "5월 11일",
                    replacementBadge = "D-5",
                    spareCount = 2,
                    averageCycleDays = 33,
                    recommendedCycleDays = 40,
                    currentUsageDays = 61,
                    historyDays = listOf(32, 28, 37, 43, 61),
                ),
            6 to
                detail(
                    id = 6,
                    title = "배터리 팩",
                    status = ConsumableDetailStatus.Healthy,
                    recentReplacementDate = "4월 9일",
                    nextReplacementDate = "5월 15일",
                    replacementBadge = "D-9",
                    spareCount = 0,
                    averageCycleDays = 31,
                    recommendedCycleDays = 60,
                    currentUsageDays = 27,
                    historyDays = listOf(27, 34, 32, 29, 27),
                ),
        )

    fun findDetail(id: Int): ConsumableDetailUiModel = detailItems[id] ?: fallbackDetail(id)

    fun completeReplacement(id: Int): ConsumableDetailUiModel {
        val current = findDetail(id)
        val updated =
            current.copy(
                status = ConsumableDetailStatus.Healthy,
                recentReplacementDate = "5월 6일",
                nextReplacementDate = "6월 5일",
                replacementBadge = "D-30",
                spareCount = (current.spareCount - 1).coerceAtLeast(0),
                currentUsageDays = 0,
                history =
                    current.history
                        .map { it.copy(isCurrent = false) }
                        .takeLast(4) +
                        ReplacementHistoryUiModel(days = 0, dateLabel = "현재", isCurrent = true),
            )

        detailItems[id] = updated
        return updated
    }

    private fun fallbackDetail(id: Int): ConsumableDetailUiModel {
        val usage = usageItems.firstOrNull { it.id == id } ?: usageItems.first()
        val status =
            if (usage.daysInUse >= 60) {
                ConsumableDetailStatus.Warning
            } else {
                ConsumableDetailStatus.Healthy
            }

        return detail(
            id = usage.id,
            title = usage.title,
            status = status,
            recentReplacementDate = "4월 1일",
            nextReplacementDate = "5월 31일",
            replacementBadge = if (status == ConsumableDetailStatus.Warning) "D+1" else "D-25",
            spareCount = 1,
            averageCycleDays = 34,
            recommendedCycleDays = 45,
            currentUsageDays = usage.daysInUse,
            historyDays = listOf(26, 28, 37, 43, usage.daysInUse),
        )
    }
}

@Immutable
data class ConsumableDetailUiModel(
    val id: Int,
    val title: String,
    val status: ConsumableDetailStatus,
    val recentReplacementDate: String,
    val nextReplacementDate: String,
    val replacementBadge: String,
    val spareCount: Int,
    val averageCycleDays: Int,
    val recommendedCycleDays: Int,
    val currentUsageDays: Int,
    val history: List<ReplacementHistoryUiModel>,
)

@Immutable
data class ReplacementHistoryUiModel(
    val days: Int,
    val dateLabel: String,
    val isCurrent: Boolean,
)

enum class ConsumableDetailStatus {
    Warning,
    Healthy,
}

private fun detail(
    id: Int,
    title: String,
    status: ConsumableDetailStatus,
    recentReplacementDate: String,
    nextReplacementDate: String,
    replacementBadge: String,
    spareCount: Int,
    averageCycleDays: Int,
    recommendedCycleDays: Int,
    currentUsageDays: Int,
    historyDays: List<Int>,
): ConsumableDetailUiModel =
    ConsumableDetailUiModel(
        id = id,
        title = title,
        status = status,
        recentReplacementDate = recentReplacementDate,
        nextReplacementDate = nextReplacementDate,
        replacementBadge = replacementBadge,
        spareCount = spareCount,
        averageCycleDays = averageCycleDays,
        recommendedCycleDays = recommendedCycleDays,
        currentUsageDays = currentUsageDays,
        history =
            historyDays.mapIndexed { index, days ->
                ReplacementHistoryUiModel(
                    days = days,
                    dateLabel =
                        if (index == historyDays.lastIndex) {
                            "현재"
                        } else {
                            HistoryDateLabels[index]
                        },
                    isCurrent = index == historyDays.lastIndex,
                )
            },
    )

private val HistoryDateLabels = listOf("1/12", "2/9", "3/8", "4/6")
