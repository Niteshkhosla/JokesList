package com.example.jokesapplication.network

import retrofit2.Response
import javax.inject.Inject

class ApiServiceImpl @Inject constructor(private val apiService: ApiService) {

    suspend fun getPost():Response<String> = apiService.getPost("Json")

}