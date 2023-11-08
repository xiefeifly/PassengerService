package updata.api

import retrofit2.Response

/**
 * 密封类形式的，网络数据封装类
 */
sealed class ApiResponse<T> {
    companion object {
        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body: T? = response.body()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()

                } else {
                    ApiSuccessResponce(body)
                }
            } else {
                ApiErrorResponse(
                    response.code(),
                    response.errorBody()?.toString() ?: response.message()
                )

            }

        }

        fun <T> create(errorCode: Int, error: Throwable): ApiResponse<T> {
            return ApiErrorResponse(
                errorCode,
                error.message ?: "Unknown Error"
            )
        }
    }
}

class ApiEmptyResponse<T> : ApiResponse<T>()
data class ApiErrorResponse<T>(val errorCode: Int, val errorMessage: String) : ApiResponse<T>()
data class ApiSuccessResponce<T>(val body: T) : ApiResponse<T>()

internal const val UNKNOWN_ERROR_CODE = -1
