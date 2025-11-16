package com.example.virtualmuseum.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO cho request body của API tạo bình luận.
 */
data class CreateCommentRequest(
    @SerializedName("fossil_id")
    val fossilId: String,
    val content: String,
    @SerializedName("parent_comment_id")
    val parentCommentId: Int? = null // Nullable vì đây là optional
)

/**
 * DTO cho một bình luận, có cấu trúc đệ quy cho các 'replies'.
 * Dùng cho cả response của API tạo và lấy danh sách bình luận.
 */
data class CommentDto(
    @SerializedName("comment_id")
    val commentId: Int,
    val content: String,
    @SerializedName("created_at")
    val createdAt: String,

    // --- CHANGE Int TO String HERE ---
    @SerializedName("fossil_id")
    val fossilId: String,

    @SerializedName("parent_comment_id")
    val parentCommentId: Int?,
    val user: CommentUserDto,
    val reactions: Map<String, Int>,
    @SerializedName("user_reaction")
    val userReaction: String?,
    val replies: List<CommentDto>,
    @SerializedName("is_hidden")
    val isHidden: Boolean,
)

/**
 * DTO cho thông tin người dùng rút gọn trong một bình luận.
 */
data class CommentUserDto(
    @SerializedName("user_id")
    val userId: Int,
    val username: String
)

/**
 * DTO cho request body của API xóa bình luận.
 */
data class DeleteCommentRequest(
    @SerializedName("comment_id")
    val commentId: Int
)

/**
 * DTO cho một item trong danh sách lịch sử bình luận.
 */
data class CommentHistoryDto(
    @SerializedName("comment_id")
    val commentId: Int,
    @SerializedName("fossil_id")
    val fossilId: String,
    @SerializedName("author_id")
    val authorId: Int,
    @SerializedName("parent_comment_id")
    val parentCommentId: Int?,
    val content: String,
    @SerializedName("is_hidden")
    val isHidden: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)