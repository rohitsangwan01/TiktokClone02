package com.pvaindia.tiktokclone0.api

import com.google.gson.annotations.SerializedName
import com.pvaindia.tiktokclone0.model.CategoryModel

data class ContentsResponse (

    @SerializedName("api_key")
    var apiKey: String?,

    @SerializedName("name")
    var name: String?,

    @SerializedName("description")
    var description: String?,

    @SerializedName("status")
    var status: String?,

    @SerializedName("last_updated_at")
    var lastUpdatedAt: Long,

    @SerializedName("categories")
    var categories: List<CategoryModel>?
)