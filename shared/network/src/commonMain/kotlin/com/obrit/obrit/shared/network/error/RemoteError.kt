package com.obrit.obrit.shared.network.error

class RemoteError(
    val statusCode: Int,
    val errorCode: Int,
    override val message: String,
) : Exception(message) {
    val httpErrorMessage: String = mapHttpError(statusCode)
}

private fun mapHttpError(code: Int): String =
    when (code) {
        400 -> "Bad Request: request payload is invalid."
        401 -> "Unauthorized: authentication is required."
        403 -> "Forbidden: you do not have permission to perform this request."
        404 -> "Not Found: the requested resource does not exist."
        in 500 until 600 -> "Internal Server Error: the server failed to process the request."
        else -> "Unknown Error: an unexpected network error occurred."
    }
