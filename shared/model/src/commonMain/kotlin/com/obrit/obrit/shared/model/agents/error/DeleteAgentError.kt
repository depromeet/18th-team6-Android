package com.obrit.obrit.shared.model.agents.error

import com.obrit.obrit.shared.model.RootError

open class DeleteAgentError : RootError() {
    class InvalidId : DeleteAgentError() {
        override val code = 20000
    }

    override fun createErrorInstances(): Array<RootError> =
        arrayOf(
            InvalidId(),
        )
}
