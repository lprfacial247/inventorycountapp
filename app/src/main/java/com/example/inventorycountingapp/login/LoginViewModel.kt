package com.example.inventorycountingapp.login

import androidx.lifecycle.ViewModel
import com.example.inventorycountingapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel() {

    fun login(deviceToken: String, pinCode: String, onSuccess: (String) -> Unit, onFailed: (String) -> Unit) {
        RetrofitClient.getApiClient()
            .login(deviceToken, pinCode)
            .enqueue(object : Callback<LoginResponse?> {
                override fun onResponse(
                    call: Call<LoginResponse?>,
                    response: Response<LoginResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()?.status == "success") {
                            onSuccess.invoke(response.body()?.message.toString())
                        }
                    } else {
                        onFailed.invoke("No response found from server")
                    }
                }

                override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                    onFailed.invoke(t.localizedMessage)
                }
            })
    }
}