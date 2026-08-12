package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.weatherapp.api.RetrofitClient
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.model.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()

        binding.btnSearch.setOnClickListener {
            val city = binding.etCity.text.toString().trim()
            if (city.isEmpty()) {
                Toast.makeText(context, "Please enter a city name.", Toast.LENGTH_SHORT).show()
            } else {
                fetchWeatherData(city)
            }
        }
    }

    private fun fetchWeatherData(city: String) {
        // UI Preparation
        binding.btnSearch.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        val apiKey = BuildConfig.OPENWEATHER_API_KEY

        RetrofitClient.instance.getCurrentWeather(city, apiKey).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                binding.btnSearch.isEnabled = true
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    Log.d("WeatherApp", "Success: ${response.code()}, City: ${weatherResponse?.cityName}")
                    
                    weatherResponse?.let {
                        // Extract and display weather information
                        binding.tvCityResult.text = it.cityName
                        binding.tvTemp.text = "${it.main.temp.toInt()}°"
                        binding.tvCondition.text = it.weather.firstOrNull()?.description?.replaceFirstChar { char ->
                            if (char.isLowerCase()) char.titlecase(java.util.Locale.getDefault()) else char.toString()
                        } ?: "Unknown"
                        binding.tvHumidity.text = "${it.main.humidity}%"
                        binding.tvWind.text = "${it.wind.speed} km/h"
                    }
                } else {
                    Log.e("WeatherApp", "HTTP Error: ${response.code()}")
                    Toast.makeText(context, "Unable to retrieve weather information.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                binding.btnSearch.isEnabled = true
                binding.progressBar.visibility = View.GONE
                
                Log.e("WeatherApp", "Network Error: ${t.message}")
                Toast.makeText(context, "Network error. Please check your internet connection.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_search_focus -> {
                        binding.etCity.requestFocus()
                        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                        imm.showSoftInput(binding.etCity, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}