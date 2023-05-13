package com.example.jokesapplication.network


import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("/api")
     suspend  fun getPost(@Query("format")format:String):Response<String>
}