package com.example.virtualmuseum.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO cho request body của API thêm reaction.
 */
data class AddReactionRequest(
    @SerializedName("comment_id")
    val commentId: Int,
    val type: String // Ví dụ: "Like", "Heart", ...
)

/**
 * DTO cho request body của API xóa reaction.
 */
data class DeleteReactionRequest(
    @SerializedName("comment_id")
    val commentId: Int
)

/**
 * DTO cho dữ liệu data trả về khi thêm reaction thành công.
 */
data class ReactionDto(
    @SerializedName("reaction_id")
    val reactionId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("comment_id")
    val commentId: Int,
    val type: String,
    @SerializedName("created_at")
    val createdAt: String
)