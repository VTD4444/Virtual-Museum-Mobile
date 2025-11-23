package com.example.virtualmuseum.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NewsDto(
    @SerializedName("news_id")
    val id: Int,
    @SerializedName("lang_code")
    val langCode: String,
    val title: String,
    val description: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("publish_date")
    val publishDate: String,
    @SerializedName("promo_start_date")
    val promoStartDate: String?,
    @SerializedName("promo_end_date")
    val promoEndDate: String?
)