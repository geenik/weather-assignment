package com.example.weather_assignment.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.weather_assignment.R
import com.example.weather_assignment.models.Sys
import com.example.weather_assignment.models.Weather
import com.example.weather_assignment.utils.utils
import com.google.android.gms.location.LocationServices
import java.util.Locale

@Composable
fun MainScreen() {
    val viewModel: MainviewModel = viewModel()
    val context= LocalContext.current
    LaunchedEffect(key1 = true) {
        getLocation(context) {
            Log.d("city", it)
            viewModel.fetchData(it)
        }
    }
    val data = viewModel.weatherData.collectAsState()
    val weather = utils.getWeatherObject(LocalContext.current, "weather")

    if (!utils.isInternetAvailable(LocalContext.current)) {
        if (weather != null) {
            MainContent(data = weather)
        } else {
            Toast.makeText(LocalContext.current, "Internet Not Available", Toast.LENGTH_SHORT)
                .show()
        }
    } else {
        when (data.value) {
            is ResultWrapper.Loading -> {
                if (weather != null) {
                    MainContent(data = weather)
                } else {
                    LinearProgressIndicator()
                }
            }
            is ResultWrapper.Success -> {
                Log.d("data", (data.value as ResultWrapper.Success<Weather>).data.toString())
                MainContent(data = (data.value as ResultWrapper.Success<Weather>).data)
                utils.saveWeatherObject(
                    LocalContext.current, "weather",
                    (data.value as ResultWrapper.Success<Weather>).data
                )
            }
            else -> {
                    Toast.makeText(LocalContext.current, "Something Went Wrong", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

}

@Composable
fun MainContent(data: Weather, modifier: Modifier = Modifier) {

    val weatherItem = data
    val imageurl = "https://openweathermap.org/img/wn/${weatherItem.weather[0].icon}.png"

    Column(
        modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = weatherItem.name + "," + weatherItem.sys.country,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.headlineLarge
        )
        Divider()
        Text(
            text = utils.formatDate(weatherItem.dt),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(6.dp)
        )

        Surface(
            modifier = Modifier
                .padding(4.dp)
                .size(200.dp),
            shape = CircleShape,
            color = Color(0xFFFFC400)
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherStateImage(imageurl)
                Text(
                    text = utils.formatDecimals(weatherItem.main.temp) + "°",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = weatherItem.weather[0].main,
                    fontStyle = FontStyle.Italic
                )
            }
        }
        HumidityWindPressureRow(weather = weatherItem)
        Divider()
        sunriseandsunset(weather = weatherItem.sys)
    }
}

@Composable
fun WeatherStateImage(imageurl: String) {
    Image(
        painter = rememberAsyncImagePainter(imageurl),
        contentDescription = "Icon Image",
        modifier = Modifier.size(80.dp)
    )
}

@Composable
fun HumidityWindPressureRow(weather: Weather) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.humidity),
                contentDescription = "humidity icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${weather.main.humidity}%",
                style = MaterialTheme.typography.labelLarge
            )

        }

        Row() {
            Icon(
                painter = painterResource(id = R.drawable.pressure),
                contentDescription = "pressure icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${weather.main.pressure} psi",
                style = MaterialTheme.typography.labelLarge
            )

        }

        Row() {
            Icon(
                painter = painterResource(id = R.drawable.wind),
                contentDescription = "wind icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${utils.formatDecimals(weather.wind.speed)} " + "m/s",
                style = MaterialTheme.typography.labelLarge
            )

        }

    }

}


@Composable
fun sunriseandsunset(weather: Sys) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.sunrise),
                contentDescription = "sunrise icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = utils.formatDateTime(weather.sunrise),
                style = MaterialTheme.typography.labelLarge
            )

        }

        Row() {
            Icon(
                painter = painterResource(id = R.drawable.sunset),
                contentDescription = "sunset icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = utils.formatDateTime(weather.sunset),
                style = MaterialTheme.typography.labelLarge
            )

        }

    }

}

fun getLocation(context: Context, function: (String) -> Unit): String {

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var city="delhi"

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return "delhi"
    }
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Use the location data
                val latitude = location.latitude
                val longitude = location.longitude
                city = getCityName(latitude,longitude,context)
                function.invoke(city)
                Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
            } else {
                Log.d("TAG", "location is null")
                // Handle the case where location is null
            }
        }
        .addOnFailureListener { e:Exception ->
            // Handle the failure to get location
            Log.e("Location", "Error getting location: ${e.message}")
        }
    return city
}

fun getCityName(latitude: Double, longitude: Double,context: Context): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

    return if (!addresses.isNullOrEmpty()) {
        addresses[0].locality ?: "Unknown City"
    } else {
        Log.d("TAG", "city is unknown")
        "Unknown City"
    }
}


