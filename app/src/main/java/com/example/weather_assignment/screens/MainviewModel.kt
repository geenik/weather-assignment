package com.example.weather_assignment.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_assignment.models.Weather
import com.example.weather_assignment.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainviewModel:ViewModel() {
    private val api=RetrofitInstance.retrofit

    private val _myDataStateFlow = MutableStateFlow<ResultWrapper<Weather>>(ResultWrapper.Loading)
    val weatherData: StateFlow<ResultWrapper<Weather>> = _myDataStateFlow

    fun fetchData(city:String="delhi") {
        viewModelScope.launch {
            try {
                _myDataStateFlow.value = ResultWrapper.Loading
                val result = api.getWeather(city)
                _myDataStateFlow.value = ResultWrapper.Success(result)
            } catch (e: Exception) {
                _myDataStateFlow.value = ResultWrapper.Error(e.message ?: "An error occurred")
            }
        }
    }
}

sealed class ResultWrapper<out T> {
    class Success<out T>(val data: T) : ResultWrapper<T>()
    class Error(val message: String) : ResultWrapper<Nothing>()
    object Loading : ResultWrapper<Nothing>()
}