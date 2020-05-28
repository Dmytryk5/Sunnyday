package com.dmytryk.sunnyday.data

sealed class ResponseData<out T>(val data: T?, val error: Throwable?) {

    class SuccessResponse<T>(data: T) : ResponseData<T>(data, null)
    class ErrorResponse<Any>(error: Throwable?) : ResponseData<Any>(null, error)
    class Loading: ResponseData<Nothing>(null, null)
}
