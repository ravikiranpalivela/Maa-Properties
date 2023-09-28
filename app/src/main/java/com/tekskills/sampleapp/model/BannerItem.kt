package com.tekskills.sampleapp.model

import com.google.gson.annotations.SerializedName

class BannerItem : ArrayList<BannerItemItem>()

data class BannerItemItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("link")
    val link: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("submittedOn")
    val submittedOn: String,
    @SerializedName("title")
    val title: String,
)