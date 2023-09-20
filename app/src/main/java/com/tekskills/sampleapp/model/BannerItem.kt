package com.tekskills.sampleapp.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


class BannerItem : ArrayList<BannerItemItem>()

@Entity(tableName = "banner_items")
data class BannerItemItem(
    @PrimaryKey
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
//    @SerializedName("UpdatedOn")
//    val updatedOn: String
)

//@Entity(tableName = "banner_items")
//data class BannerItems(
//    @PrimaryKey
//    @SerializedName("id")
//    val id: Int,
//    val status: String,
//    val name: String,
//    val submittedOn: String,
//    val link: String,
//    val updatedOn: String,
//    val title: String
//)