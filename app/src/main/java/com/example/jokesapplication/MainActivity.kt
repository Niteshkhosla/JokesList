package com.example.jokesapplication



import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jokesapplication.adapter.JokesAdapter
import com.example.jokesapplication.databinding.ActivityMainBinding
import com.example.jokesapplication.util.ApiState
import com.example.jokesapplication.util.Utils
import com.example.jokesapplication.viewmodel.JokesViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Type
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var sharedPreferences: SharedPreferences
    private var jokesList=ArrayList<String>()
    private lateinit var timerObj:CountDownTimer
    private lateinit var binding: ActivityMainBinding
    private val jokesViewModel: JokesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val json = sharedPreferences.getString(Utils.KEYS_JOKES_LIST, null)
        if(json!=null && json.isNotEmpty()){
            val type: Type = object : TypeToken<ArrayList<String>>() {}.type
            jokesList = Gson().fromJson<Any>(json, type) as ArrayList<String>
            setAdapter(jokesList)
            startTimer()
        }else{
            startTimer()
        }

        lifecycleScope.launchWhenStarted {
            jokesViewModel._postStateFlow.collect { it ->
                when (it) {
                    is ApiState.Loading -> {}
                    is ApiState.Failure -> {}
                    is ApiState.Empty -> {}

                    is ApiState.Success -> {
                        binding.JokesRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

                        if(jokesList.size<Utils.NUMBER_OF_JOKES_LIST){
                            jokesList.add(it.data.body()!!)
                            jokesViewModel.saveArrayList(jokesList)
                            setAdapter(jokesList)
                        } else{
                            val randomNumber = (0..9).random()
                            jokesList.set(randomNumber,it.data.body()!!)
                            jokesViewModel.saveArrayList(jokesList)
                            AdapterRefresh(jokesList)
                            Toast.makeText(this@MainActivity,"New Jokes updated at Position$ randomNumber",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun timerRefresh():CountDownTimer{
        val timer =object :CountDownTimer(Utils.TIME_IN_MILLIS.toLong(),Utils.COUNT_DOWN_INTERVAL.toLong()){

            override fun onTick(S: Long) {}

            override fun onFinish() {
           timerObj.cancel()
           timerObj=timerRefresh()
           timerObj.start()
           jokesViewModel.getPost()
            }
        }
        return timer
    }

    private fun startTimer(){
        timerObj=timerRefresh()
        timerObj.start()
    }

    private fun setAdapter(jokesList:ArrayList<String>){
        val adapter=JokesAdapter(jokesList)
        binding.JokesRecyclerView.adapter = adapter
    }

    private fun AdapterRefresh(jokesList:ArrayList<String>){
        val adapter=JokesAdapter(jokesList)
        adapter.notifyDataSetChanged()
    }
}