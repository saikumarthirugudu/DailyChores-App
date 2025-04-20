package com.example.dailychoresapp.ui.utils

fun formatDate(year: Int, month: Int, day: Int): String {
    // Formats the date as DD/MM/YYYY
    return "${day.toString().padStart(2, '0')}/${(month + 1).toString().padStart(2, '0')}/$year"
}
