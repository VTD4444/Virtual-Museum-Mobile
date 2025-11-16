package com.example.virtualmuseum.data.remote.dto

import com.google.gson.annotations.SerializedName

// --- DTOs cho API GET /fossils/{fossilId} ---
// (Chúng ta sẽ cần cái này cho màn hình chi tiết)
data class FossilDetailData(
    val fossil: FossilDetailDto
)

data class FossilDetailDto(
    @SerializedName("fossil_id")
    val fossilId: String,
    val name: String,
    val origin: String,
    val period: String,
    @SerializedName("lang_code")
    val langCode: String,
    val description: String,
    @SerializedName("model3d_url")
    val model3dUrl: String,
    @SerializedName("qr_code")
    val qrCode: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val liked: Int,
    @SerializedName("is_favorited")
    val isFavorited: Boolean?,

    val size: String?,
    val weight: String?,
    @SerializedName("special_ability")
    val specialAbility: String?
)

// --- DTOs cho API POST /fossils/search ---
// (Đây là các lớp gây ra lỗi)

data class SearchRequest(
    val q: String? = null,
    val period: String? = null,
    val origin: String? = null,
    val limit: Int? = null,
    val offset: Int? = null,
    @SerializedName("sort_by")
    val sortBy: String? = null,
    @SerializedName("sort_order")
    val sortOrder: String? = null
)

data class SearchResponse(
    val message: String,
    val data: List<FossilSearchResultDto>,
    val total: Int,
    val limit: Int,
    val offset: Int,
    @SerializedName("total_page")
    val totalPage: Int
)

data class FossilSearchResultDto(
    @SerializedName("fossil_id")
    val fossilId: String,
    val name: String,
    val origin: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("created_at")
    val createdAt: String
)