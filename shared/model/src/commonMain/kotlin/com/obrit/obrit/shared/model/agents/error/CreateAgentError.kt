package com.obrit.obrit.shared.model.agents.error

import com.obrit.obrit.shared.model.RootError

open class CreateAgentError: RootError() {

    class EmptyName: CreateAgentError() {
        override val code = 10000
    }

    class DuplicatedName: CreateAgentError() {
        override val code = 10001
    }

    class InvalidNameFormat: CreateAgentError() {
        override val code = 10002
    }

    class InvalidAgentType: CreateAgentError() {
        override val code = 10003
    }

    override fun createErrorInstances(): Array<RootError> {
        return arrayOf(
            EmptyName(),
            DuplicatedName(),
            InvalidNameFormat(),
            InvalidAgentType()
        )
    }
}
