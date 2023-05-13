package com.example.jokesapplication.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokesapplication.repository.MainRepository
import com.example.jokesapplication.util.ApiState
import com.example.jokesapplication.util.Utils
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject


@HiltViewModel
class JokesViewModel@Inject constructor(private val mainRepository: MainRepository,
                                        private val sharedPreferences: SharedPreferences) : ViewModel(){

    private val postStateFlow: MutableStateFlow<ApiState>
            = MutableStateFlow(ApiState.Empty)

    val _postStateFlow: StateFlow<ApiState> = postStateFlow

    fun getPost() = viewModelScope.launch {
        postStateFlow.value = ApiState.Loading
        mainRepository.getPost()
            .catch { e->
                postStateFlow.value=ApiState.Failure(e)
            }.collect { data->
                postStateFlow.value=ApiState.Success(data)
            }
    }

    fun saveArrayList(JokesList: ArrayList<String>) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json: String = gson.toJson(JokesList)
        editor.putString(Utils.KEYS_JOKES_LIST, json)
        editor.apply()
    }
}