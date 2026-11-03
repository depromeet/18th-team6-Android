package com.obrit.feature.register.viewmodel

enum class ItemNameError {
    TooLong,
    InvalidCharacter,
    InvalidSpacing,
    Duplicated,
}

internal fun validateItemName(
    name: String,
    existingNames: Set<String>,
): ItemNameError? =
    when {
        name.length > ITEM_NAME_MAX_LENGTH -> ItemNameError.TooLong
        !ITEM_NAME_ALLOWED_CHARACTERS.matches(name) -> ItemNameError.InvalidCharacter
        name != name.trim() || "  " in name -> ItemNameError.InvalidSpacing
        name in existingNames -> ItemNameError.Duplicated
        else -> null
    }

// 소모품 추가·상세 기능명세서 3.2: 같은 이름이 있으면 1부터 번호를 붙인다(칫솔 → 칫솔 1).
// 사용 중이지 않은 가장 작은 번호를 쓰고 15자를 넘으면 소모품명 뒤쪽을 줄이는 것은 온보딩 기능명세서 5.2를 따른다.
internal fun nextAvailableItemName(
    baseName: String,
    existingNames: Set<String>,
): String {
    if (baseName !in existingNames) return baseName
    return generateSequence(ITEM_NAME_FIRST_NUMBER) { number -> number + 1 }
        .map { number ->
            val suffix = " $number"
            baseName.take(ITEM_NAME_MAX_LENGTH - suffix.length).trimEnd() + suffix
        }.first { candidate -> candidate !in existingNames }
}

internal const val ITEM_NAME_MAX_LENGTH = 15

private const val ITEM_NAME_FIRST_NUMBER = 1

// 한글 조합 중에 오류가 깜빡이지 않도록 자모도 허용한다.
private val ITEM_NAME_ALLOWED_CHARACTERS = Regex("^[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9 ]*$")
