package com.plcoding.cryptotracker.widget.presentation.helper

import java.util.Locale

internal fun formatPrice(priceUsd: Double): String =
    "$" + String.format(Locale.US, "%,.2f", priceUsd)
