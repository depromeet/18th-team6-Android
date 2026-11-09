package com.obrit.obrit.navigation

import androidx.compose.runtime.saveable.Saver
import com.obrit.feature.register.viewmodel.ReceiptDraftItem
import com.obrit.obrit.shared.model.items.ReplacementPeriod
import com.obrit.obrit.shared.model.receipts.AnalyzedItem
import com.obrit.obrit.shared.model.receipts.ReceiptAnalysis

// RegisterNavigation이 화면 사이에 넘기는 값을 회전과 프로세스 종료 뒤에도 되살리기 위한 Saver.
// shared model은 저장 방식을 알지 않도록(Model.md) 저장 형태 변환은 여기에만 둔다.

private const val KEY_RECEIPT_IMAGE_URL = "receiptImageUrl"
private const val KEY_PURCHASED_DATE = "purchasedDate"
private const val KEY_ITEMS = "items"

private const val KEY_SUGGESTED_NAME = "suggestedName"
private const val KEY_CATEGORY_ID = "categoryId"
private const val KEY_SUGGESTED_CATEGORY_NAME = "suggestedCategoryName"
private const val KEY_ICON_URL = "iconUrl"
private const val KEY_QUANTITY = "quantity"
private const val KEY_SUGGESTED_REPLACEMENT_INTERVAL_DAYS = "suggestedReplacementIntervalDays"

private const val KEY_ID = "id"
private const val KEY_NAME = "name"
private const val KEY_LAST_REPLACEMENT_PERIOD = "lastReplacementPeriod"
private const val KEY_NEW_CATEGORY_NAME = "newCategoryName"
private const val KEY_NEW_CATEGORY_DEFAULT_REPLACEMENT_INTERVAL_DAYS = "newCategoryDefaultReplacementIntervalDays"

internal val ReceiptAnalysisSaver: Saver<ReceiptAnalysis?, Any> =
    Saver(
        save = { analysis -> analysis?.toSaveable() },
        restore = { saved -> (saved as Map<*, *>).toReceiptAnalysis() },
    )

internal val ReceiptDraftItemsSaver: Saver<List<ReceiptDraftItem>, Any> =
    Saver(
        save = { items -> items.map { it.toSaveable() } },
        restore = { saved -> (saved as List<*>).map { (it as Map<*, *>).toReceiptDraftItem() } },
    )

private fun ReceiptAnalysis.toSaveable(): Map<String, Any?> =
    mapOf(
        KEY_RECEIPT_IMAGE_URL to receiptImageUrl,
        KEY_PURCHASED_DATE to purchasedDate,
        KEY_ITEMS to items.map { it.toSaveable() },
    )

private fun Map<*, *>.toReceiptAnalysis(): ReceiptAnalysis =
    ReceiptAnalysis(
        receiptImageUrl = get(KEY_RECEIPT_IMAGE_URL) as String,
        purchasedDate = get(KEY_PURCHASED_DATE) as String?,
        items = (get(KEY_ITEMS) as List<*>).map { (it as Map<*, *>).toAnalyzedItem() },
    )

private fun AnalyzedItem.toSaveable(): Map<String, Any?> =
    mapOf(
        KEY_SUGGESTED_NAME to suggestedName,
        KEY_CATEGORY_ID to categoryId,
        KEY_SUGGESTED_CATEGORY_NAME to suggestedCategoryName,
        KEY_ICON_URL to iconUrl,
        KEY_QUANTITY to quantity,
        KEY_SUGGESTED_REPLACEMENT_INTERVAL_DAYS to suggestedReplacementIntervalDays,
    )

private fun Map<*, *>.toAnalyzedItem(): AnalyzedItem =
    AnalyzedItem(
        suggestedName = get(KEY_SUGGESTED_NAME) as String,
        categoryId = get(KEY_CATEGORY_ID) as Long?,
        suggestedCategoryName = get(KEY_SUGGESTED_CATEGORY_NAME) as String,
        iconUrl = get(KEY_ICON_URL) as String,
        quantity = get(KEY_QUANTITY) as Int,
        suggestedReplacementIntervalDays = get(KEY_SUGGESTED_REPLACEMENT_INTERVAL_DAYS) as Int,
    )

private fun ReceiptDraftItem.toSaveable(): Map<String, Any?> =
    mapOf(
        KEY_ID to id,
        KEY_NAME to name,
        KEY_QUANTITY to quantity,
        KEY_LAST_REPLACEMENT_PERIOD to lastReplacementPeriod?.name,
        KEY_CATEGORY_ID to categoryId,
        KEY_NEW_CATEGORY_NAME to newCategoryName,
        KEY_NEW_CATEGORY_DEFAULT_REPLACEMENT_INTERVAL_DAYS to newCategoryDefaultReplacementIntervalDays,
    )

private fun Map<*, *>.toReceiptDraftItem(): ReceiptDraftItem =
    ReceiptDraftItem(
        id = get(KEY_ID) as Long,
        name = get(KEY_NAME) as String,
        quantity = get(KEY_QUANTITY) as Int,
        lastReplacementPeriod =
            (get(KEY_LAST_REPLACEMENT_PERIOD) as String?)?.let { name ->
                ReplacementPeriod.entries.firstOrNull { it.name == name }
            },
        categoryId = get(KEY_CATEGORY_ID) as Long?,
        newCategoryName = get(KEY_NEW_CATEGORY_NAME) as String?,
        newCategoryDefaultReplacementIntervalDays = get(KEY_NEW_CATEGORY_DEFAULT_REPLACEMENT_INTERVAL_DAYS) as Int?,
    )
