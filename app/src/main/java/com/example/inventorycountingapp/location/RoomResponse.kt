package com.example.inventorycountingapp.location


import com.google.gson.annotations.SerializedName

data class RoomResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("floor_idx")
        val floorIdx: Int,
        @SerializedName("index")
        val index: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("whouse_idx")
        val whouseIdx: Int
    )
}