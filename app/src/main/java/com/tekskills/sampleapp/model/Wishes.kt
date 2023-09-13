package com.tekskills.sampleapp.model
import com.google.gson.annotations.SerializedName

class Wishes : ArrayList<WishesItem>()

data class WishesItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageFilePath")
    val imageFilePath: String,
    @SerializedName("imageName")
    val imageName: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("scheduleDate")
    val scheduleDate: String,
    @SerializedName("title")
    val title: String
)