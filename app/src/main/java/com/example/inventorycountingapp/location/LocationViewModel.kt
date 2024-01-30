package com.example.inventorycountingapp.location

import androidx.lifecycle.ViewModel
import com.example.inventorycountingapp.common.network.RetrofitClient
import com.example.inventorycountingapp.common.pref.SpManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationViewModel: ViewModel() {
    fun fetchFloor(wireHouseId: Int, onSuccess: (FloorResponse) -> Unit, onFailed: (String) -> Unit) {
        RetrofitClient.getApiClient()
            .fetchFloor(wireHouseId)
            .enqueue(object : Callback<FloorResponse?> {
                override fun onResponse(
                    call: Call<FloorResponse?>,
                    response: Response<FloorResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()?.status == "success") {
                            onSuccess.invoke(response.body()!!)
                        }
                    } else {
                        onFailed.invoke("No response found from server")
                    }
                }

                override fun onFailure(call: Call<FloorResponse?>, t: Throwable) {
                    onFailed.invoke(t.localizedMessage)
                }
            })
    }

    fun fetchRoom(wireHouseId: Int, floorId: Int, onSuccess: (RoomResponse) -> Unit, onFailed: (String) -> Unit) {
        RetrofitClient.getApiClient()
            .fetchRoom(wireHouseId, floorId)
            .enqueue(object : Callback<RoomResponse?> {
                override fun onResponse(
                    call: Call<RoomResponse?>,
                    response: Response<RoomResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()?.status == "success") {
                            onSuccess.invoke(response.body()!!)
                        }
                    } else {
                        onFailed.invoke("No response found from server")
                    }
                }

                override fun onFailure(call: Call<RoomResponse?>, t: Throwable) {
                    onFailed.invoke(t.localizedMessage)
                }
            })
    }

    fun fetchSection(wireHouseId: Int, roomId: Int, onSuccess: (SectionResponse) -> Unit, onFailed: (String) -> Unit) {
        RetrofitClient.getApiClient()
            .fetchSection(wireHouseId, roomId)
            .enqueue(object : Callback<SectionResponse?> {
                override fun onResponse(
                    call: Call<SectionResponse?>,
                    response: Response<SectionResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()?.status == "success") {
                            onSuccess.invoke(response.body()!!)
                        }
                    } else {
                        onFailed.invoke("No response found from server")
                    }
                }

                override fun onFailure(call: Call<SectionResponse?>, t: Throwable) {
                    onFailed.invoke(t.localizedMessage)
                }
            })
    }
}