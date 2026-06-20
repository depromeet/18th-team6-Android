package com.obrit.obrit.shared.network.response.item

import com.obrit.obrit.shared.model.ReplacementDate
import com.obrit.obrit.shared.model.items.ItemDetail
import com.obrit.obrit.shared.model.items.ItemDetailCategory
import com.obrit.obrit.shared.model.items.ItemDetailReplacement
import com.obrit.obrit.shared.model.items.ItemDetailStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemDetailResponse(
    @SerialName("itemId") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("category") val category: ItemCategoryResponse? = null,
    @SerialName("iconUrl") val iconUrl: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("dday") val dDay: Int? = null,
    @SerialName("ddayLabel") val dDayLabel: String? = null,
    @SerialName("spareQuantity") val spareQuantity: Int? = null,
    @SerialName("lastReplacedDate") val lastReplacedDate: String? = null,
    @SerialName("nextReplacementDate") val nextReplacementDate: String? = null,
    @SerialName("usedDays") val usedDays: Int? = null,
    @SerialName("myAverageCycleDays") val myAverageCycleDays: Double? = null,
    @SerialName("recommendedCycleDays") val recommendedCycleDays: Int? = null,
    @SerialName("progressPercentage") val progressPercentage: Double? = null,
    @SerialName("recentReplacements") val recentReplacements: List<ItemReplacementResponse> = emptyList(),
)

@Serializable
data class ItemCategoryResponse(
    @SerialName("categoryId") val id: Long,
    @SerialName("name") val name: String,
)

@Serializable
data class ItemReplacementResponse(
    @SerialName("replacementId") val id: Long,
    @SerialName("date") val date: String,
    @SerialName("cycleDays") val cycleDays: Int? = null,
    @SerialName("current") val current: Boolean? = null,
)

fun ItemDetailResponse.toItemDetail() =
    ItemDetail(
        id = id,
        name = name,
        category =
            ItemDetailCategory(
                id = category?.id ?: UNKNOWN_CATEGORY_ID,
                name = category?.name.orEmpty(),
            ),
        iconUrl = iconUrl,
        status = status.toItemDetailStatus(),
        dDay = dDay,
        dDayLabel = dDayLabel.orEmpty(),
        spareQuantity = spareQuantity ?: 0,
        lastReplacedDate = lastReplacedDate?.let(::ReplacementDate),
        nextReplacementDate = nextReplacementDate?.let(::ReplacementDate),
        usedDays = usedDays ?: 0,
        myAverageCycleDays = myAverageCycleDays ?: 0.0,
        recommendedCycleDays = recommendedCycleDays ?: 0,
        progressPercentage = progressPercentage ?: 0.0,
        recentReplacements = recentReplacements.map { response -> response.toItemDetailReplacement() },
    )

private fun ItemReplacementResponse.toItemDetailReplacement() =
    ItemDetailReplacement(
        id = id,
        date = ReplacementDate(date),
        cycleDays = cycleDays ?: 0,
        current = current == true,
    )

private fun String?.toItemDetailStatus(): ItemDetailStatus =
    ItemDetailStatus.entries.firstOrNull { status -> status.name == this } ?: ItemDetailStatus.UNKNOWN

private const val UNKNOWN_CATEGORY_ID = 0L
