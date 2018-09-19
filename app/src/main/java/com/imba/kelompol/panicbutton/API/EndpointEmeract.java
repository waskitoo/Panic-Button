package com.imba.kelompol.panicbutton.API;

import com.imba.kelompol.panicbutton.Models.API.Article.ArticleResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EndpointEmeract {
    // Weather
    @GET("/api/weather")
    Call<Map<String,Object>> getCurrentWeather();

    // News
    @GET("/api/news")
    Call<ArticleResponse> getListNews();
}
