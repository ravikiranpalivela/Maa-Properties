package com.tekskills.sampleapp.model

import com.google.gson.annotations.SerializedName
import com.tekskills.sampleapp.utils.video.isValidUrl
import com.tekskills.sampleapp.utils.video.validateOneString
import com.tekskills.sampleapp.utils.video.validateOneValue
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NewsItem : ArrayList<NewsItemDetails>()

data class NewsItemDetails(
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
    @SerializedName("source") val source: Source,
    var videoDataUrl: String,
    var imageDataUrl: String,
    var descriptionData: String,
    var titleData: String,
    var publishedData: String,
    var youtubeId: String,
) : Serializable {
    init {
        descriptionData =
            if (this.description.isNullOrEmpty() && this.videoDescription.isNullOrEmpty())
                ""
            else
                validateOneString(this.description, this.videoDescription)

        titleData = if (this.title.isNullOrEmpty())
            ""
        else
            this.title

        publishedData = if (this.scheduleDate.isNullOrEmpty()) {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } else
            this.scheduleDate

        if (validateOneValue(this.videoPath, this.videoFilePath) != "null"
            && !this.videoUrl.isNullOrEmpty()
        ) {
            if (this.videoUrl.isValidUrl())
                videoDataUrl = videoUrl
            else if (validateOneValue(this.videoPath, this.videoFilePath)?.isValidUrl()!!)
                videoDataUrl = validateOneValue(this.videoPath, this.videoFilePath)!!
        } else if (validateOneValue(this.videoPath, this.videoFilePath) == "null"
            && !this.videoUrl.isNullOrEmpty()
        ) {
            if (this.videoUrl.isValidUrl())
                videoDataUrl = videoUrl
            else {
                if (validateOneValue(this.imageFilePath, this.imagePath) != "null"
                    && !this.imageUrl.isNullOrEmpty()
                ) {
                    imageDataUrl = this.imageUrl
                } else if (!validateOneValue(
                        this.imageFilePath,
                        this.imagePath
                    ).equals("null") && this.imageUrl.isNullOrEmpty()
                ) {
                    imageDataUrl = validateOneValue(this.imageFilePath, this.imagePath)
                }
            }
        } else if (validateOneValue(this.videoPath, this.videoFilePath) != "null"
            && this.videoUrl.isNullOrEmpty()
        ) {
            if (validateOneValue(this.videoPath, this.videoFilePath)?.isValidUrl()!!)
                videoDataUrl = (validateOneValue(this.videoPath, this.videoFilePath)!!)
        } else {
            if (!validateOneValue(
                    this.imageFilePath,
                    this.imagePath
                ).equals("null") && !this.imageUrl.isNullOrEmpty()
            ) {

                imageDataUrl = this.imageUrl
            } else if (!validateOneValue(
                    this.imageFilePath,
                    this.imagePath
                ).equals("null") && this.imageUrl.isNullOrEmpty()
            ) {

                imageDataUrl = validateOneValue(this.imageFilePath, this.imagePath)
            } else {

                imageDataUrl = this.imageUrl
            }
        }
    }
}