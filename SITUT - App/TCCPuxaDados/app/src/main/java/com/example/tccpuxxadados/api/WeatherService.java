package com.example.tccpuxxadados.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("weather")
    Call<WeatherModel> getWeather(@Query("key") String key, @Query("city_name") String city_name);

}
