package com.example.arto.ui.common.bottomsheet

import android.app.DatePickerDialog
import android.content.Context
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatePickerHelper(private val context: Context) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun showDatePicker(
        editText: EditText,
        initialDate: String? = null,
        onDateSelected: ((String) -> Unit)? = null
    ) {
        val calendar = Calendar.getInstance()

        // Parse initial date if provided
        initialDate?.let {
            try {
                val date = dateFormat.parse(it)
                date?.let { calendar.time = it }
            } catch (e: Exception) {
                // Use current date if parsing fails
            }
        }

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val selectedDate = calendar.time

                // Set display format in EditText
                editText.setText(displayFormat.format(selectedDate))

                // Return API format through callback
                val apiFormattedDate = dateFormat.format(selectedDate)
                onDateSelected?.invoke(apiFormattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    fun formatDateForDisplay(apiDate: String): String {
        return try {
            val date = dateFormat.parse(apiDate)
            date?.let { displayFormat.format(it) } ?: apiDate
        } catch (e: Exception) {
            apiDate
        }
    }

    fun formatDateForApi(displayDate: String): String {
        return try {
            val date = displayFormat.parse(displayDate)
            date?.let { dateFormat.format(it) } ?: displayDate
        } catch (e: Exception) {
            displayDate
        }
    }
}