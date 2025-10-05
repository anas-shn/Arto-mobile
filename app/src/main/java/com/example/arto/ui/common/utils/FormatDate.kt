package com.example.arto.ui.common.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object FormatDate {

    fun dateDMY(dateString: String): String {
        return try {

            // Ambil bagian tanggal saja (sebelum 'T')
            val datePart = dateString.split("T")[0] // "2025-09-30"

            // Split berdasarkan '-'
            val parts = datePart.split("-") // ["2025", "09", "30"]

            if (parts.size == 3) {
                val year = parts[0].substring(2) // "25" (ambil 2 digit terakhir)
                val month = parts[1] // "09"
                val day = parts[2] // "30"

                val result = "$day-$month-$year" // "30-09-25"
                return result
            } else {
                Log.e("FormatDate", "Invalid date format")
                return "Invalid Date"
            }

        } catch (e: Exception) {
            Log.e("FormatDate", "Error: ${e.message}")
            return "Invalid Date"
        }
    }
    
    /**
     * Get time only (HH:mm)
     * Input: "2025-09-30T11:57:26.245512+00:00"
     * Output: "11:57"
     */
    fun timeOnly(dateString: String): String {
        return try {
            // Ambil bagian waktu (setelah 'T', sebelum '.')
            val timePart = dateString.split("T")[1].split(".")[0] // "11:57:26"
            val timeComponents = timePart.split(":") // ["11", "57", "26"]
            
            if (timeComponents.size >= 2) {
                "${timeComponents[0]}:${timeComponents[1]}" // "11:57"
            } else {
                "00:00"
            }
        } catch (e: Exception) {
            Log.e("FormatDate", "Error parsing time: ${e.message}")
            "00:00"
        }
    }
    
    /**
     * Get full date (dd-MM-yyyy)
     * Input: "2025-09-30T11:57:26.245512+00:00"
     * Output: "30-09-2025"
     */
    fun dateFull(dateString: String): String {
        return try {
            val datePart = dateString.split("T")[0] // "2025-09-30"
            val parts = datePart.split("-") // ["2025", "09", "30"]
            
            if (parts.size == 3) {
                val year = parts[0] // "2025"
                val month = parts[1] // "09"
                val day = parts[2] // "30"
                
                "$day-$month-$year" // "30-09-2025"
            } else {
                "Invalid Date"
            }
        } catch (e: Exception) {
            Log.e("FormatDate", "Error: ${e.message}")
            "Invalid Date"
        }
    }
}


