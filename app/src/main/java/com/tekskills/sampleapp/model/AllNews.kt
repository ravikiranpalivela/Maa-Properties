package com.tekskills.sampleapp.model

import com.google.gson.annotations.SerializedName
import com.tekskills.sampleapp.utils.video.isValidUrl
import com.tekskills.sampleapp.utils.video.validateOneString
import com.tekskills.sampleapp.utils.video.validateOneValue
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AllNews : ArrayList<AllNewsItem>()

data class AllNewsItem(
    @SerializedName("description")
    val description: String,
    @SerializedName("newsId")
    val newsId: Int,
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
//    @SerializedName("source") val source: Source,
) : Serializable