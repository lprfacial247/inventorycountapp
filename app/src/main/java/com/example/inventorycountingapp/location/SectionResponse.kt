package com.example.inventorycountingapp.location


import com.google.gson.annotations.SerializedName

data class SectionResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("index")
        val index: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("room_idx")
        val roomIdx: Int,
        @SerializedName("whouse_idx")
        val whouseIdx: Int
    )
}