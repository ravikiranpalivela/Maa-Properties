package com.tekskills.sampleapp.model
import com.google.gson.annotations.SerializedName


class PosterItem : ArrayList<PosterItemDetails>()

data class PosterItemDetails(
    @SerializedName("backgroundImage")
    val backgroundImage: String?,
    @SerializedName("backgroundImagePath")
    val backgroundImagePath: String,
    @SerializedName("mainImage")
    val mainImage: String?,
    @SerializedName("posterId")
    val posterId: Int,
    @SerializedName("posterImagePath")
    val posterImagePath: String,
    @SerializedName("posterName")
    val posterName: String,
    @SerializedName("submitedBy")
    val submitedBy: String,
    @SerializedName("submittedOn")
    val submittedOn: String,
    @SerializedName("ImgID")
    val imageID: Int
)