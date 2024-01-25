package com.example.inventorycountingapp.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.inventorycountingapp.R
import com.example.inventorycountingapp.app.MyApplication
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

object RetrofitClient {

    var gson: Gson? = GsonBuilder()
        .setLenient()
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor { log ->
        Timber.tag("REST-API").v(log)
    }.apply { level = HttpLoggingInterceptor.Level.BODY }


    private val okHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(10, TimeUnit.SECONDS)
        readTimeout(10, TimeUnit.SECONDS)
        writeTimeout(10, TimeUnit.SECONDS)
        addInterceptor(loggingInterceptor)
        addInterceptor(ResponseErrorInterceptor())
    }.build()
    


    fun getApiClient(): ApiService {
        val chuckerInterceptor = ChuckerInterceptor.Builder(MyApplication.appContext).apply {
            //maxContentLength(10000)
       }.build()
        val newOkHttpClient = okHttpClient.newBuilder()
            .addInterceptor(chuckerInterceptor)
            .build()
        return Retrofit.Builder().apply {
            baseUrl(MyApplication.appContext.getString(R.string.base_url))
            client(newOkHttpClient)
            addConverterFactory(GsonConverterFactory.create(gson))
        }.build().create(ApiService::class.java)
    }
    
}