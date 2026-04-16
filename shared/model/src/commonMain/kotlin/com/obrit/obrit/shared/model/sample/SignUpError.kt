package com.obrit.obrit.shared.model.sample

import com.obrit.obrit.shared.model.error.RootError

open class SignUpError : RootError() {
    class InvalidEmail : SignUpError() {
        override val code: Int = 2001
    }

    class WeakPassword : SignUpError() {
        override val code: Int = 2002
    }

    class DuplicatedEmail : SignUpError() {
        override val code: Int = 2003
    }

    class InvalidNickname : SignUpError() {
        override val code: Int = 2004
    }

    override fun createErrorInstances(): Array<RootError> = arrayOf(
        InvalidEmail(),
        WeakPassword(),
        DuplicatedEmail(),
        InvalidNickname(),
    )
}
