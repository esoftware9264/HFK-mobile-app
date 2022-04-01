package com.esoftwere.hfk.network


sealed class ResultWrapper<T> {
    data class Progress<T>(var isLoading: Boolean) : ResultWrapper<T>()
    data class Success<T>(var data: T?) : ResultWrapper<T>()
    data class Failure<T>(val error: String?) : ResultWrapper<T>()

    companion object {
        fun <T> loading(isLoading: Boolean): ResultWrapper<T> = Progress(isLoading)
        fun <T> success(data: T?): ResultWrapper<T>? = Success(data)
        fun <T> failure(msg: String?): ResultWrapper<T> = Failure(msg)
    }
}