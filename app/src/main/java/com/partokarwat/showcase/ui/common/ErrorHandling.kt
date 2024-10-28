package com.partokarwat.showcase.ui.common

import com.partokarwat.showcase.R
import retrofit2.HttpException
import java.io.IOException

fun getErrorStringRes(exception: Throwable?): Int =
    when (exception) {
        is HttpException ->
            when (exception.code()) {
                in 400..499 -> R.string.client_error_text
                in 500..599 -> R.string.server_error_text
                else -> R.string.http_error_text
            }
        is IOException -> R.string.network_error_text
        else -> R.string.unknown_error_text
    }
