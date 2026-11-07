package com.obrit.obrit.shared.model.categories.error

import com.obrit.obrit.shared.model.RootError

open class CreateCategoryError : RootError() {
    // 서버가 409로만 구분하므로 errorCode는 사용하지 않고 repository에서 HTTP status로 매핑한다.
    class DuplicatedName : CreateCategoryError()

    override fun createErrorInstances(): Array<RootError> = arrayOf(DuplicatedName())
}
