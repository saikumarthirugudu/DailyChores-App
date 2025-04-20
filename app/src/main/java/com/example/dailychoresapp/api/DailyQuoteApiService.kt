package com.example.dailychoresapp.api

import retrofit2.Response
import retrofit2.http.GET

data class QuoteResponse(
    val content: String,
    val author: String
)

interface DailyQuoteApiService {

    @GET("random")
    suspend fun getDailyQuote(): Response<QuoteResponse>
}
