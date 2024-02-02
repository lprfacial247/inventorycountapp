package com.example.inventorycountingapp.product

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.inventorycountingapp.common.network.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
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


    fun updateProductImage(productIdx: RequestBody, fileName: RequestBody, file: MultipartBody.Part, onSuccess: (String) -> Unit, onFailed: (String) -> Unit) {
        RetrofitClient.getApiClient()
            .updateProductImage(productIdx, fileName, file)
            .enqueue(object : Callback<ApiResponse?> {
                override fun onResponse(
                    call: Call<ApiResponse?>,
                    response: Response<ApiResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()?.status == "success") {
                            onSuccess.invoke("Image Changed")
                        }
                        else {
                            onFailed.invoke("Failed")
                        }
                    } else {
                        onFailed.invoke("No response found from server")
                    }
                }

                override fun onFailure(call: Call<ApiResponse?>, t: Throwable) {
                    onFailed.invoke(t.localizedMessage)
                    Log.e("ApiOnFailure", "onFailure: "+ t.localizedMessage)
                }
            })
    }
}