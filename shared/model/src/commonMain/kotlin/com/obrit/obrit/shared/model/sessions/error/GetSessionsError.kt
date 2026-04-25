package com.obrit.obrit.shared.model.sessions.error

import com.obrit.obrit.shared.model.RootError

open class GetSessionsError : RootError() {
    override fun createErrorInstances(): Array<RootError> = arrayOf()
}
