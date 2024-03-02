package com.example.launchpad.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuild {
    companion object
    {
        private const val BASE_URL : String= "http://10.0.2.2:8000/api/"
        fun build(): Route{
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .baseUrl(BASE_URL)
                .build()
                .create(Route::class.java)
        }
    }
}