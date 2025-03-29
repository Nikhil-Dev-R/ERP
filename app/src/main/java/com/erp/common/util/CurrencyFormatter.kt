package com.erp.common.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Utility class for currency formatting across the app.
 */
object CurrencyFormatter {
    /**
     * Returns currency formatter configured for Indian Rupees
     */
    fun getIndianRupeeFormatter(): NumberFormat {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        numberFormat.currency = Currency.getInstance("INR")
        numberFormat.minimumFractionDigits = 0
        numberFormat.maximumFractionDigits = 2
        return numberFormat
    }
    
    /**
     * Format the given amount as Indian Rupees
     */
    fun formatAsRupees(amount: Number): String {
        return getIndianRupeeFormatter().format(amount)
    }
} 