package com.example.dictionaryplusplus.data.remote

sealed interface ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>
    data class Error(val errorType: ErrorType = ErrorType.UNKNOWN) : ApiResponse<Nothing>
    data object Loading : ApiResponse<Nothing>
}
