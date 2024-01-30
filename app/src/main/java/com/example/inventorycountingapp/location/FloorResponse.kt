package com.example.inventorycountingapp.location


import com.google.gson.annotations.SerializedName

data class FloorResponse(
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
        @SerializedName("whouse_idx")
        val whouseIdx: Int
    )
}