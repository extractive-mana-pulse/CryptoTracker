package com.plcoding.cryptotracker.core.presentation

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

internal fun formatSize(bytes: Int): String {
    val mb = bytes / (1024.0 * 1024.0)
    return String.format("%.2f MB", mb)
}