package com.example.dailychoresapp.api

import com.example.dailychoresapp.data.model.Quote
import retrofit2.Response
import retrofit2.http.GET

interface ZenQuoteApiService {

    @GET("random")
    suspend fun getDailyQuote(): Response<List<Quote>>
}