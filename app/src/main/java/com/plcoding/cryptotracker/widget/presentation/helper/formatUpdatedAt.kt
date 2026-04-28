package com.plcoding.cryptotracker.widget.presentation.helper

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal fun formatUpdatedAt(millis: Long): String {
    if (millis <= 0L) return "Never"
    return DateTimeFormatter.ofPattern("HH:mm")
        .format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))
}
