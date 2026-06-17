package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable

/**
 * 영수증 Result → Detail 화면 간 핸드오프 모델.
 * 기존 카테고리면 [categoryId], 신규 카테고리면 [newCategoryName] +
 * [newCategoryDefaultReplacementIntervalDays]를 채워 전달한다.
 */
@Immutable
data class ReceiptDraftItem(
    val name: String,
    val quantity: Int,
    val categoryId: Long?,
    val newCategoryName: String?,
    val newCategoryDefaultReplacementIntervalDays: Int?,
)
