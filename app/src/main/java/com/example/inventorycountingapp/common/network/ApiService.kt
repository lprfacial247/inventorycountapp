package com.example.inventorycountingapp.common.network


import com.example.inventorycountingapp.location.FloorResponse
import com.example.inventorycountingapp.location.RoomResponse
import com.example.inventorycountingapp.location.SectionResponse
import com.example.inventorycountingapp.login.LoginResponse
import com.example.inventorycountingapp.product.ApiResponse
import com.example.inventorycountingapp.product.ProductResponse
import com.example.inventorycountingapp.wirehouse.WirehouseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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


    @FormUrlEncoded
    @POST("api-get-product")
    fun getProduct(
        @Field("barcode") barCode: String
    ): Call<ProductResponse>

    @FormUrlEncoded
    @POST("api-get-floor")
    fun fetchFloor(
        @Field("whouse_idx") wireHouseIndex: Int
    ): Call<FloorResponse>

    @FormUrlEncoded
    @POST("api-get-room")
    fun fetchRoom(
        @Field("whouse_idx") wireHouseIndex: Int,
        @Field("floor_idx") floorIndex: Int,
    ): Call<RoomResponse>

    @FormUrlEncoded
    @POST("api-get-section")
    fun fetchSection(
        @Field("whouse_idx") wireHouseIndex: Int,
        @Field("room_idx") roomIndex: Int,
    ): Call<SectionResponse>

    @Multipart
    @POST("api-update-product-image")
    fun updateProductImage(
        @Part("product_idx") productIdx: RequestBody,
        @Part("file_name") fileName: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<ApiResponse>

}