package com.example.virtualmuseum.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Lớp generic để xử lý cấu trúc response chung của API.
 * @param T Kiểu dữ liệu của trường "data" (ví dụ: UserDto, LoginResponseData).
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? // data có thể null nếu request thất bại
)

/**
 * DTO cho request body của API Đăng ký.
 */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

/**
 * DTO cho request body của API Đăng nhập.
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * DTO chứa dữ liệu trả về trong "data" của API Đăng nhập thành công.
 */
data class LoginResponseData(
    val token: String,
    val user: UserDto,
    @SerializedName("expires_in") // Ánh xạ "expires_in" trong JSON sang "expiresIn"
    val expiresIn: Int
)

/**
 * DTO chứa thông tin người dùng, được sử dụng lại ở nhiều nơi.
 */
data class UserDto(
    @SerializedName("user_id") // Ánh xạ "user_id" trong JSON sang "userId"
    val userId: Int,
    val username: String,
    val email: String,
    val role: String
)

/**
 * DTO cho request body của API Đổi mật khẩu.
 */
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

/**
 * DTO cho một mẫu vật trong danh sách yêu thích.
 * Lưu ý: Cấu trúc này khác với FossilDto ban đầu, nên ta tạo class mới.
 */
data class FavoriteFossilDto(
    @SerializedName("fossil_id")
    val fossilId: String,
    val name: String,
    val origin: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("created_at")
    val createdAt: String
)

/**
 * DTO cho request body của API Thêm/Xóa Yêu thích.
 */
data class FavoriteRequest(
    @SerializedName("fossil_id")
    val fossilId: String
)