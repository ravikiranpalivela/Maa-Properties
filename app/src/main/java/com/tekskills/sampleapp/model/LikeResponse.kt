package com.tekskills.sampleapp.model
import com.google.gson.annotations.SerializedName


data class LikeResponse(
    @SerializedName("message")
    val message: String
)