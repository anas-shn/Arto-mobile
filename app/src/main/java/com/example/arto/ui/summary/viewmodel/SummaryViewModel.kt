package com.example.arto.ui.summary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arto.data.model.TransactionItem
import com.example.arto.data.repository.TransactionRepository
import com.example.arto.ui.summary.adapter.TransactionGroup
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SummaryViewModel : ViewModel() {

    private val transactionRepository = TransactionRepository()

    private val _transactionGroups = MutableLiveData<List<TransactionGroup>>()
    val transactionGroups: LiveData<List<TransactionGroup>> = _transactionGroups

    private val _totalIncome = MutableLiveData<Int>()
    val totalIncome: LiveData<Int> = _totalIncome

    private val _totalExpense = MutableLiveData<Int>()
    val totalExpense: LiveData<Int> = _totalExpense

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var allTransactions: List<TransactionItem> = emptyList()
    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH)
    private val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR) // Dynamic current year

    init {
        fetchTransactions()
    }

    fun fetchTransactions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                allTransactions = transactionRepository.getTransactions()
                filterAndGroupTransactions()

            } catch (e: Exception) {
                _error.value = "Failed to fetch transactions: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByMonth(month: Int) {
        selectedMonth = month
        filterAndGroupTransactions()
    }

    private fun filterAndGroupTransactions() {
        if (allTransactions.isEmpty()) return

        // Filter by selected month and current year only
        val filteredTransactions = allTransactions.filter { transaction ->
            val transactionDate = parseTransactionDate(transaction.created_at)
            val calendar = Calendar.getInstance().apply { time = transactionDate }

            calendar.get(Calendar.MONTH) == selectedMonth &&
            calendar.get(Calendar.YEAR) == currentYear
        }

        // Group transactions by date
        val groupedTransactions = groupTransactionsByDate(filteredTransactions)
        _transactionGroups.value = groupedTransactions

        // Calculate totals
        calculateTotals(filteredTransactions)
    }

    private fun groupTransactionsByDate(transactions: List<TransactionItem>): List<TransactionGroup> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))

        return transactions
            .groupBy { transaction ->
                val date = parseTransactionDate(transaction.created_at)
                dateFormat.format(date)
            }
            .map { (dateString, transactions) ->
                val date = dateFormat.parse(dateString)!!
                val displayDate = getRelativeDisplayDate(date, displayFormat)

                // Calculate total for this group
                val totalAmount = transactions.sumOf { transaction ->
                    if (transaction.type.equals("income", ignoreCase = true)) {
                        transaction.amount
                    } else {
                        -transaction.amount
                    }
                }

                TransactionGroup(
                    date = dateString,
                    displayDate = displayDate,
                    transactions = transactions.sortedByDescending { parseTransactionDate(it.created_at) },
                    totalAmount = totalAmount
                )
            }
            .sortedByDescending { it.date }
    }

    private fun getRelativeDisplayDate(date: Date, displayFormat: SimpleDateFormat): String {
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val transactionCal = Calendar.getInstance().apply { time = date }

        return when {
            isSameDay(today, transactionCal) -> "Hari ini"
            isSameDay(yesterday, transactionCal) -> "Kemarin"
            else -> displayFormat.format(date)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun parseTransactionDate(dateString: String): Date {
        val formats = arrayOf(
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        )

        for (format in formats) {
            try {
                return format.parse(dateString) ?: Date()
            } catch (e: Exception) {
                continue
            }
        }
        return Date() // Fallback to current date
    }

    private fun calculateTotals(transactions: List<TransactionItem>) {
        var income = 0
        var expense = 0

        transactions.forEach { transaction ->
            if (transaction.type.equals("income", ignoreCase = true)) {
                income += transaction.amount
            } else {
                expense += transaction.amount
            }
        }

        _totalIncome.value = income
        _totalExpense.value = expense
    }

    // Simplified month filter - 12 months only with current year
    fun getMonthFilterData(): List<Pair<String, Int>> {
        val months = mutableListOf<Pair<String, Int>>()
        val monthFormat = SimpleDateFormat("MMMM", Locale("id", "ID"))

        // Add all 12 months for current year
        repeat(12) { monthIndex ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, currentYear)
                set(Calendar.MONTH, monthIndex)
                set(Calendar.DAY_OF_MONTH, 1)
            }

            val monthName = monthFormat.format(calendar.time)
            months.add(monthName to monthIndex)
        }

        return months
    }


    fun refreshData() {
        fetchTransactions()
    }

    fun clearError() {
        _error.value = null
    }
}