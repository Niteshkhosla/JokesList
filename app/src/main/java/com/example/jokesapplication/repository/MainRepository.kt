package com.example.jokesapplication.repository

import android.util.Log
import com.example.jokesapplication.network.ApiServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiServiceImpl: ApiServiceImpl) {

        fun getPost():Flow<Response<String>> = flow {
            emit(apiServiceImpl.getPost())
        }.flowOn(Dispatchers.IO)
    }