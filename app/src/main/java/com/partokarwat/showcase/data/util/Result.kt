package com.partokarwat.showcase.data.util

sealed interface Result<out T> {
    object Loading : Result<Nothing>

    data class Success<out T>(
        val data: T,
    ) : Result<T>

    data class Error(
        val exception: Throwable,
    ) : Result<Nothing>

    fun getOrNull(): T? =
        when (this) {
            is Success -> data
            is Loading, is Error -> null
        }

    fun getErrorOrNull(): Throwable? =
        when (this) {
            is Error -> exception
            is Loading, is Success -> null
        }

    fun getOrThrow(): T =
        when (this) {
            is Error -> throw this.exception
            is Loading -> throw IllegalStateException("Result is loading")
            is Success -> data
        }

    val isError: Boolean
        get() = this is Error
}
