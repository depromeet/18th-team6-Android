package com.obrit.obrit.shared.model.agents.error

import com.obrit.obrit.shared.model.RootError

open class GetAgentsError : RootError() {
    override fun createErrorInstances(): Array<RootError> = arrayOf()
}
