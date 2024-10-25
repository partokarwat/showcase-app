package com.partokarwat.showcase.ui.common

import com.partokarwat.showcase.R
import retrofit2.HttpException
import java.io.IOException

fun getErrorStringRes(exception: Throwable?): Int =
    when (exception) {
        is HttpException -> R.string.init_coin_details_error_text
        is IOException -> R.string.network_error_text
        else -> R.string.unknown_error_text
    }
