package com.example.dailychoresapp.repository

import com.example.dailychoresapp.api.DailyQuoteApiService
import com.example.dailychoresapp.api.QuoteResponse
import retrofit2.Response

class QuoteRepository(private val apiService: DailyQuoteApiService) {

    suspend fun getDailyQuote(): Response<QuoteResponse> {
        return apiService.getDailyQuote()
    }
}
