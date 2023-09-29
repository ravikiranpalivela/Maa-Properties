package com.tekskills.sampleapp.model

import com.google.gson.annotations.SerializedName

class PosterDetails : ArrayList<PosterItem>()

data class PosterItem(
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
    val imageID: Int,
    @SerializedName("comments")
    val comments: CommentDetails,
    @SerializedName("likes")
    val likes: Int,
    @SerializedName("share")
    val share: Int
)