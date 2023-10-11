package com.tekskills.sampleapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AllNewsDetailsData(
    @SerializedName("news")
    val news: AllNewsDetails,
    @SerializedName("wish")
    val wish: AllNewsDetails,
    @SerializedName("sort")
    val sort: AllNewsDetails,
    @SerializedName("poster")
    val poster: AllNewsDetails
)

class AllNewsDetails : ArrayList<AllNewsItem?>()

data class AllNewsItem(
    @SerializedName("description")
    val description: String,
    @SerializedName("newsId")
    val newsId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageFilePath")
    var imageFilePath: String = "",
    @SerializedName("imagePath")
    val imagePath: String,
    @SerializedName("imageName")
    val imageName: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("inputlanguage")
    val inputlanguage: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("postType")
    val postType: String,
    @SerializedName("scheduleDate")
    val scheduleDate: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("videoFilePath")
    val videoFilePath: String,
    @SerializedName("videoDescription")
    val videoDescription: String,
    @SerializedName("videoPath")
    val videoPath: String,
    @SerializedName("videoName")
    val videoName: String,
    @SerializedName("videoUrl")
    val videoUrl: String,
    @SerializedName("websiteUrl")
    val websiteUrl: String,
    @SerializedName("comments")
    val comments: CommentDetails,
    @SerializedName("likes")
    val likes: Int,
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
    var submittedOn: String,
    @SerializedName("ImgID")
    val imageID: Int,
    @SerializedName("link")
    val link: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("newsType")
    var newsType: String,
    @SerializedName("share")
    val share: Int
//    @SerializedName("source") val source: Source,
) : Serializable
