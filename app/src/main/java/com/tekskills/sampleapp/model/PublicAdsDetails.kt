package com.tekskills.sampleapp.model

import com.google.gson.annotations.SerializedName

class PublicAdsDetails : ArrayList<PublicAdsDetailsItem>()

data class PublicAdsDetailsItem(
    @SerializedName("description")
    val description: String,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("fileName")
    val fileName: String,
    @SerializedName("filePath")
    val filePath: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("title")
    val title: String
)