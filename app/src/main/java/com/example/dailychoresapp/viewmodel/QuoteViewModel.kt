package com.example.dailychoresapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailychoresapp.data.model.Quote
import com.example.dailychoresapp.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuoteViewModel(private val repository: QuoteRepository) : ViewModel() {

    private val _quote = MutableStateFlow<Quote?>(null)
    val quote: StateFlow<Quote?> get() = _quote

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    init {
        fetchDailyQuote()
    }

    private fun fetchDailyQuote() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getDailyQuote()
                if (response.isSuccessful && response.body() != null) {
                    val quoteResponse = response.body()!!
                    _quote.value = Quote(
                        content = quoteResponse.content,
                        author = quoteResponse.author ?: "Unknown"
                    )
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Failed to load quote"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
