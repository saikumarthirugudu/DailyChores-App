package com.example.dailychoresapp.repository

import com.example.dailychoresapp.api.ZenQuoteApiService

class QuoteRepository(private val api: ZenQuoteApiService) {
    suspend fun getDailyQuote() = api.getDailyQuote()
}