package com.obrit.obrit.shared.model.items.error

import com.obrit.obrit.shared.model.RootError

open class GetItemError : RootError() {
    class NotFound : GetItemError()

    override fun createErrorInstances(): Array<RootError> = arrayOf(NotFound())
}
