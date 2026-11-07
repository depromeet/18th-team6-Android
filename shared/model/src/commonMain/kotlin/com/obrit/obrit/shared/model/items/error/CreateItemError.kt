package com.obrit.obrit.shared.model.items.error

import com.obrit.obrit.shared.model.RootError

open class CreateItemError : RootError() {
    class DuplicatedName : CreateItemError()

    override fun createErrorInstances(): Array<RootError> = arrayOf(DuplicatedName())
}
