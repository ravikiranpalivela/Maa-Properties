package com.tekskills.sampleapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NewsDetails : ArrayList<NewsItem>()

data class NewsItem(
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
    @SerializedName("share")
    val share: Int,
    @SerializedName("pollDetails")
    val pollDetails: PollDetails,
//    @SerializedName("pollOptions")
//    val pollOptions: List<String>,
//    @SerializedName("pollTitle")
//    val pollTitle: String,
//    @SerializedName("isPoll")
//    val isPoll: String,
//    @SerializedName("source") val source: Source,
) : Serializable
class CommentDetails : ArrayList<Comment>()

data class Comment(
    @SerializedName("comment")
    val comment: String,
    @SerializedName("commentedOn")
    val commentedOn: String
)