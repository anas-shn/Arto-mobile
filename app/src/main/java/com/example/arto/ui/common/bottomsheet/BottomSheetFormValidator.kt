package com.example.arto.ui.common.bottomsheet

import android.content.Context
import android.widget.AutoCompleteTextView
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout

class BottomSheetFormValidator(private val context: Context) {

    fun validateRequired(
        editText: EditText?,
        textInputLayout: TextInputLayout?,
        errorMessage: String
    ): Boolean {
        return if (editText?.text.isNullOrBlank()) {
            textInputLayout?.error = errorMessage
            false
        } else {
            textInputLayout?.error = null
            true
        }
    }

    fun validateNumeric(
        editText: EditText?,
        textInputLayout: TextInputLayout?,
        errorMessage: String,
        allowZero: Boolean = false
    ): Boolean {
        val text = editText?.text.toString()
        val number = text.toIntOrNull()

        return if (text.isBlank() || number == null || (!allowZero && number <= 0)) {
            textInputLayout?.error = errorMessage
            false
        } else {
            textInputLayout?.error = null
            true
        }
    }

    fun validateLong(
        editText: EditText?,
        textInputLayout: TextInputLayout?,
        errorMessage: String
    ): Boolean {
        val text = editText?.text.toString()
        return if (text.isBlank() || text.toLongOrNull() == null) {
            textInputLayout?.error = errorMessage
            false
        } else {
            textInputLayout?.error = null
            true
        }
    }

    fun validateAutoComplete(
        autoCompleteTextView: AutoCompleteTextView?,
        textInputLayout: TextInputLayout?,
        errorMessage: String
    ): Boolean {
        return if (autoCompleteTextView?.text.isNullOrBlank()) {
            textInputLayout?.error = errorMessage
            false
        } else {
            textInputLayout?.error = null
            true
        }
    }

    fun validateDateSelection(
        selectedDate: String,
        textInputLayout: TextInputLayout?,
        errorMessage: String
    ): Boolean {
        return if (selectedDate.isBlank()) {
            textInputLayout?.error = errorMessage
            false
        } else {
            textInputLayout?.error = null
            true
        }
    }
}