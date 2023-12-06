package com.tekskills.sampleapp.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PollDetails(
    @SerializedName("ipAddress")
    val ipAddress: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("newsId")
    val newsId: Int,
    @SerializedName("noOfPolling")
    val noOfPolling: Int,
    @SerializedName("options")
    val options: List<String>,
    @SerializedName("pollId")
    val pollId: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("submittedOn")
    val submittedOn: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("optionDetails")
    val optionDetails: List<OptionDetail>,
): Serializable

data class OptionDetail(
    @SerializedName("count")
    val count: Int,
    @SerializedName("option")
    val option: String
): Serializable

data class ItemOption(
    val title: String,
    val vote: Int,
    val percentage: Int,
    val voted: Boolean
): Serializable