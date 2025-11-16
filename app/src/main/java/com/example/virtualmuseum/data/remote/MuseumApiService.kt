package com.example.virtualmuseum.data.remote

import com.example.virtualmuseum.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Header
import retrofit2.http.HTTP
import com.example.virtualmuseum.data.remote.dto.CommentDto
import com.example.virtualmuseum.data.remote.dto.CreateCommentRequest
import com.example.virtualmuseum.data.remote.dto.CommentHistoryDto
import com.example.virtualmuseum.data.remote.dto.DeleteCommentRequest
import com.example.virtualmuseum.data.remote.dto.AddReactionRequest
import com.example.virtualmuseum.data.remote.dto.DeleteReactionRequest
import com.example.virtualmuseum.data.remote.dto.ReactionDto

interface MuseumApiService {
    /**
     * API Đăng ký tài khoản mới.
     */
    @POST("users/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): ApiResponse<UserDto>

    /**
     * API Đăng nhập.
     */
    @POST("users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<LoginResponseData>

    /**
     * API Đổi mật khẩu.
     * AuthInterceptor sẽ tự động thêm token.
     */
    @POST("users/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): ApiResponse<Unit> // Dùng Unit vì "data" không có nội dung

    /**
     * API Lấy danh sách yêu thích.
     * AuthInterceptor sẽ tự động thêm token.
     */
    @GET("users/favorites")
    suspend fun getFavorites(
        @Header("accept-language") language: String
    ): ApiResponse<List<FavoriteFossilDto>>

    /**
     * API Thêm một mẫu vật vào danh sách yêu thích.
     */
    @POST("users/add-fossil-to-favorite")
    suspend fun addFavorite(
        @Body request: FavoriteRequest
    ): ApiResponse<Unit>

    /**
     * API Xóa một mẫu vật khỏi danh sách yêu thích.
     * Dùng @HTTP vì @DELETE không hỗ trợ @Body một cách chính thức.
     */
    @HTTP(method = "DELETE", path = "users/remove-fossil-from-favorite", hasBody = true)
    suspend fun removeFavorite(
        @Body request: FavoriteRequest
    ): ApiResponse<Unit>

    /**
     * API Tìm kiếm nâng cao.
     */
    @POST("fossils/search")
    suspend fun searchFossils(
        @Body request: SearchRequest
    ): SearchResponse

    /**
     * API Tạo bình luận mới.
     * Yêu cầu xác thực, AuthInterceptor sẽ xử lý.
     */
    @POST("comments/create-comment")
    suspend fun createComment(
        @Body request: CreateCommentRequest
    ): ApiResponse<CommentDto>

    /**
     * API Lấy danh sách bình luận của một mẫu vật.
     */
    @GET("comments/getAllComments/{fossil_id}/")
    suspend fun getComments(
        // CHANGE Int TO String HERE:
        @Path("fossil_id") fossilId: String,
        @Header("accept-language") language: String
    ): ApiResponse<List<CommentDto>>

    /**
     * API Xóa một bình luận.
     * Yêu cầu xác thực, AuthInterceptor sẽ xử lý.
     */
    @HTTP(method = "DELETE", path = "comments/delete-comment", hasBody = true)
    suspend fun deleteComment(
        @Body request: DeleteCommentRequest
    ): ApiResponse<Unit>

    /**
     * API Lấy lịch sử bình luận của người dùng.
     * Yêu cầu xác thực, AuthInterceptor sẽ xử lý.
     */
    @GET("comments/history")
    suspend fun getCommentHistory(): ApiResponse<List<CommentHistoryDto>>

    /**
     * API Thêm hoặc sửa reaction cho một bình luận.
     * Yêu cầu xác thực, AuthInterceptor sẽ xử lý.
     */
    @POST("reactions")
    suspend fun addOrUpdateReaction(
        @Body request: AddReactionRequest
    ): ApiResponse<ReactionDto>

    /**
     * API Xóa reaction của người dùng khỏi một bình luận.
     * Yêu cầu xác thực, AuthInterceptor sẽ xử lý.
     */
    @HTTP(method = "DELETE", path = "reactions/delete-reaction", hasBody = true)
    suspend fun deleteReaction(
        @Body request: DeleteReactionRequest
    ): ApiResponse<Unit>

    /**
     * API Lấy thông tin chi tiết của một mẫu vật.
     */
    @GET("fossils/{fossilId}") // Endpoint từ API doc
    suspend fun getFossilDetail(
        @Path("fossilId") fossilId: String,
        @Header("accept-language") language: String
    ): ApiResponse<FossilDetailData> // Kiểu trả về từ API doc
}