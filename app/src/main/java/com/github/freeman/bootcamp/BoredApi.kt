package com.github.freeman.bootcamp

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface BoredApi {
    @GET("activity")
    fun getActivity(): Call<BoredActivity>
}