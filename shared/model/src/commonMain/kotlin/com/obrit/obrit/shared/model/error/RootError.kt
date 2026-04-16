package com.obrit.obrit.shared.model.error

abstract class RootError : Exception() {
    open val code: Int = 0

    // TODO : KSP 자동화
    abstract fun createErrorInstances(): Array<RootError>
}
