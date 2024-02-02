package com.example.inventorycountingapp.product

import androidx.lifecycle.ViewModel
import com.example.inventorycountingapp.common.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel: ViewModel() {
    val selectedList: MutableList<ProductResponse.Data> by lazy { ArrayList() }

    fun getProduct(barCode: String, onSuccess: (ProductResponse) -> Unit, onFailed: (String) -> Unit) {
        RetrofitClient.getApiClient()
            .getProduct(barCode)
            .enqueue(object : Callback<ProductResponse?> {
                override fun onResponse(
                    call: Call<ProductResponse?>,
                    response: Response<ProductResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()?.status == "success") {
                            onSuccess.invoke(response.body()!!)
                        }
                        else {
                            onFailed.invoke("The product information is not in the server. So it can not process for that product. Sorry.")
                        }
                    } else {
                        onFailed.invoke("No response found from server")
                    }
                }

                override fun onFailure(call: Call<ProductResponse?>, t: Throwable) {
                    onFailed.invoke(t.localizedMessage)
                }
            })
    }
}