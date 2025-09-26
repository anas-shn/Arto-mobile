package com.example.arto.ui.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BudgetViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Halaman Budget"
    }
    val text: LiveData<String> = _text
}
