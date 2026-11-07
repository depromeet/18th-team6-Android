package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.obrit.shared.model.items.ReplacementPeriod

/**
 * 영수증 Result → Detail 화면 간 핸드오프 모델이자, Detail 편집값을 nav에 보존하는 durable 모델.
 * 기존 카테고리면 [categoryId], 신규 카테고리면 [newCategoryName] +
 * [newCategoryDefaultReplacementIntervalDays]를 채워 전달한다.
 *
 * [id]는 Result 항목 → draft → Detail form까지 동일하게 전파되어, back/forward 재진입 시
 * 항목 구성과 편집값을 id 기준으로 병합하는 데 쓰인다.
 */
@Immutable
data class ReceiptDraftItem(
    val id: Long,
    val name: String,
    val quantity: Int,
    val lastReplacementPeriod: ReplacementPeriod?,
    val categoryId: Long?,
    val newCategoryName: String?,
    val newCategoryDefaultReplacementIntervalDays: Int?,
)
