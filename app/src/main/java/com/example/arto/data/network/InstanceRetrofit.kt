package com.example.arto.data.network

import com.example.arto.ui.common.utils.Constant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object InstanceRetrofit {
    

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constant.BASE_URL )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
