package com.obrit.obrit.shared.network.error

import com.obrit.obrit.shared.model.RootError
import kotlin.coroutines.cancellation.CancellationException

inline fun <T : RootError, R> runCatchingWith(
    errorType: T,
    block: () -> R,
): Result<R> =
    try {
        Result.success(block())
    } catch (exception: RemoteError) {
        errorType
            .createErrorInstances()
            .find { definedError -> definedError.code == exception.errorCode }
            ?.let(Result.Companion::failure)
            ?: Result.failure(exception)
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Throwable) {
        Result.failure(exception)
    }

inline fun <R> runCatchingWith(block: () -> R): Result<R> =
    try {
        Result.success(block())
    } catch (exception: RemoteError) {
        Result.failure(exception)
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Throwable) {
        Result.failure(exception)
    }
