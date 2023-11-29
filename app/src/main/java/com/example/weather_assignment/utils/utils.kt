package com.example.weather_assignment.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.weather_assignment.models.Weather
import com.google.gson.Gson
import java.text.SimpleDateFormat

object utils {
    fun formatDate(timestamp: Int): String {
        val sdf = SimpleDateFormat("EEE, MMM d")
        val date = java.util.Date(timestamp.toLong() * 1000)

        return sdf.format(date)
    }

    fun formatDateTime(timestamp: Int): String {
        val sdf = SimpleDateFormat("hh:mm:aa")
        val date = java.util.Date(timestamp.toLong() * 1000)

        return sdf.format(date)
    }

    fun formatDecimals(item: Double): String {
        return " %.0f".format(item)
    }


    // Convert object to JSON
    fun toJson(weather: Weather): String {
        return Gson().toJson(weather)
    }

    // Convert JSON to object
    fun fromJson(json: String): Weather {
        return Gson().fromJson(json, Weather::class.java)
    }

    fun saveWeatherObject(context: Context, key: String, weather: Weather) {
        val json = toJson(weather)
        val sharedPreferences = context.getSharedPreferences("City", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, json).apply()
    }

    // Retrieve object from SharedPreferences
    fun getWeatherObject(context: Context, key: String): Weather? {
        val sharedPreferences = context.getSharedPreferences("City", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(key, null)
        return json?.let { fromJson(it) }
    }


    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities =
            connectivityManager.getNetworkCapabilities(network)

        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}