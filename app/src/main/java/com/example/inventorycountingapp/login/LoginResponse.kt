package com.example.inventorycountingapp.login


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("status")
    val status: String = ""
) {
    data class Data(
        @SerializedName("created_date")
        val createdDate: String = "",
        @SerializedName("image_path")
        val imagePath: String = "",
        @SerializedName("lst_visit_date_from")
        val lstVisitDateFrom: String = "",
        @SerializedName("lst_visit_date_to")
        val lstVisitDateTo: String = "",
        @SerializedName("lst_whouse_index")
        val lstWhouseIndex: Int = 0,
        @SerializedName("user_idx")
        val userIdx: Int = 0,
        @SerializedName("user_name")
        val userName: String = "",
        @SerializedName("lst_user")
        val lstUser: String = "",
        @SerializedName("user_role")
        val userRole: String = ""


    )
}