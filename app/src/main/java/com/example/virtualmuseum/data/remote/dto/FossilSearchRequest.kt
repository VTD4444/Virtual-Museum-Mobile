package com.example.virtualmuseum.data.remote.dto

data class FossilSearchRequest(
    val q: String? = null,
    val period: String? = null,
    val origin: String? = null,
    val limit: Int = 10,
    val offset: Int = 0,
    val sort_by: String = "name",
    val sort_order: String = "asc"
)