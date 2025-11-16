package com.example.virtualmuseum.utils

/**
 * Một lớp generic để chứa dữ liệu cùng với trạng thái của nó.
 * @param T Kiểu dữ liệu được bao bọc.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Trạng thái thành công, chứa dữ liệu.
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Trạng thái lỗi, chứa thông báo lỗi.
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /**
     * Trạng thái đang tải.
     */
    class Loading<T>(data: T? = null) : Resource<T>(data)
}