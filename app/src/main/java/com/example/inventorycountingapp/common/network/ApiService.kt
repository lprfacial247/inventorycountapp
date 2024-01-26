package com.example.inventorycountingapp.common.network


import com.example.inventorycountingapp.login.LoginResponse
import com.example.inventorycountingapp.wirehouse.WirehouseResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("api-login")
    fun login(
        @Field("device_token") deviceToken: String,
        @Field("pincode") pincode: String
    ): Call<LoginResponse>


    @GET("api-get-warehouses")
    fun fetchWireHouse(): Call<WirehouseResponse>

}