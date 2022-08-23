package com.esoftwere.hfk.base

import android.util.Log
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.utils.ValidationHelper
import retrofit2.Response

abstract class BaseRemoteAPIResponse {
    private val TAG: String = "BaseRemoteAPIResponse"

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                }
            }
            return remoteAPIError("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            Log.e(TAG, ValidationHelper.optionalBlankText(e.message))
            return remoteAPIError(e.message ?: e.toString())
        }
    }

    private fun <T> remoteAPIError(errorMessage: String): NetworkResult<T> = NetworkResult.Error(errorMessage)
}