package com.example.inventorycountingapp.wirehouse


import com.google.gson.annotations.SerializedName

data class WirehouseResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("building")
        val building: String,
        @SerializedName("cash")
        val cash: String,
        @SerializedName("city")
        val city: String,
        @SerializedName("country")
        val country: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("index")
        val index: Int,
        @SerializedName("kind")
        val kind: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("owner")
        val owner: String,
        @SerializedName("state")
        val state: String,
        @SerializedName("street")
        val street: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("zipcode")
        val zipcode: String
    )
}