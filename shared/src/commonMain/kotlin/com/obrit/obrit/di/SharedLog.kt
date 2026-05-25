package com.obrit.obrit.di

internal object SharedLog {
    fun enter(
        scope: String,
        event: String,
        details: String = "",
    ) {
        println(format("enter", scope, event, details))
    }

    fun success(
        scope: String,
        event: String,
        details: String = "",
    ) {
        println(format("success", scope, event, details))
    }

    fun failure(
        scope: String,
        event: String,
        throwable: Throwable,
        details: String = "",
    ) {
        println("${format("failure", scope, event, details)} error=${throwable::class.simpleName}: ${throwable.message.orEmpty()}")
    }

    private fun format(
        status: String,
        scope: String,
        event: String,
        details: String,
    ): String {
        val suffix = details.takeIf(String::isNotBlank)?.let { " $it" }.orEmpty()
        return "[OBRit][$scope][$status] $event$suffix"
    }
}
