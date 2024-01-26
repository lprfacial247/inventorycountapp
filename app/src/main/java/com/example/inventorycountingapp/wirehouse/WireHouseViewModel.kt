package com.example.inventorycountingapp.wirehouse

import androidx.lifecycle.ViewModel
import com.example.inventorycountingapp.common.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WireHouseViewModel: ViewModel() {

    fun fetchWireHouse(onSuccess: (WirehouseResponse) -> Unit, onFailed: (String) -> Unit) {
        RetrofitClient.getApiClient()
            .fetchWireHouse()
            .enqueue(object : Callback<WirehouseResponse?> {
                override fun onResponse(
                    call: Call<WirehouseResponse?>,
                    response: Response<WirehouseResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()?.status == "success") {
                            onSuccess.invoke(response.body()!!)
                        }
                    } else {
                        onFailed.invoke("No response found from server")
                    }
                }

                override fun onFailure(call: Call<WirehouseResponse?>, t: Throwable) {
                    onFailed.invoke(t.localizedMessage)
                }
            })
    }
}