package com.example.jokesapplication.util

import retrofit2.Response

sealed class ApiState {
    object Loading : ApiState()
    class Failure(val msg:Throwable) : ApiState()
    class Success(val data: Response<String>) : ApiState()
    object Empty : ApiState()
}