package com.example.arto.ui.common.utils

import java.text.NumberFormat
import java.util.Locale

object FormatCurenrency {

    public fun format(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0

        return format.format(amount).replace("IDR", "Rp")
    }
}