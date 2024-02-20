package com.example.weatherretrofit

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.weatherretrofit.utils.RetrofitInstance
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var temperatureTV: TextView
    private lateinit var cityTV: TextView
    private lateinit var weatherIV: ImageView
    private lateinit var windDegreeTV: TextView
    private lateinit var windSpeedTV: TextView
    private lateinit var descriptionTV: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        temperatureTV = findViewById(R.id.temperatureTV)
        cityTV = findViewById(R.id.cityTV)
        weatherIV = findViewById(R.id.weatherIV)
        windDegreeTV = findViewById(R.id.windDegreeTV)
        windSpeedTV = findViewById(R.id.windSpeedTV)
        descriptionTV = findViewById(R.id.descriptionTV)

        getCurrentWeather()
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentWeather() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                RetrofitInstance.api.getCurrentWeather(
                    "Сызрань",
                    "metric",
                    applicationContext.getString(R.string.api_key)
                )
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "app error ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val data = response.body()
                    cityTV.text = data!!.name
                    temperatureTV.text = "${data.main.temp.toString()}°С"
                    windDegreeTV.text = "${data.wind.deg.toString()}°"
                    windSpeedTV.text = "${data.wind.speed.toString()} m/sec"
                    val iconId = data.weather[0].icon
                    val imgUrl = "https://openweathermap.org/img/wn/$iconId@4x.png"
                    Picasso.get().load(imgUrl).into(weatherIV)
                    val convertPressure = (data.main.pressure / 1.33).toInt()
                    descriptionTV.text = "$convertPressure mm Hg"
                }
            }
        }
    }
}



