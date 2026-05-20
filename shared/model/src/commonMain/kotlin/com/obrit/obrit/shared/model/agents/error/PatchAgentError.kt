package com.obrit.obrit.shared.model.agents.error

import com.obrit.obrit.shared.model.RootError

open class PatchAgentError : RootError() {
    class EmptyName : PatchAgentError() {
        override val code = 10000
    }

    class DuplicatedName : PatchAgentError() {
        override val code = 10001
    }

    class InvalidNameFormat : PatchAgentError() {
        override val code = 10002
    }

    class InvalidAgentType : PatchAgentError() {
        override val code = 10003
    }

    override fun createErrorInstances(): Array<RootError> =
        arrayOf(
            EmptyName(),
            DuplicatedName(),
            InvalidNameFormat(),
            InvalidAgentType(),
        )
}
