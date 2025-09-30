package com.example.arto.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arto.data.model.TransactionItem
import com.example.arto.data.model.WalletItem
import com.example.arto.data.repository.TransactionRepository
import com.example.arto.data.repository.WalletRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val walletRepository = WalletRepository()
    private val transactionRepository = TransactionRepository()

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome Back,"
    }
    val text: LiveData<String> = _text

    // FIX: Ubah ke Double dan remove initial value calculation
    private val _totalBalance = MutableLiveData<Int>().apply {
        value = 0
    }
    val totalBalance: LiveData<Int> = _totalBalance

    private val _totalIncome = MutableLiveData<Int>().apply {
        value = 0
    }
    val totalIncome: LiveData<Int> = _totalIncome

    private val _totalOutcome = MutableLiveData<Int>().apply {
        value = 0
    }
    val totalOutcome: LiveData<Int> = _totalOutcome

    private val _wallets = MutableLiveData<List<WalletItem>>()
    val wallets: LiveData<List<WalletItem>> = _wallets

    private val _transactions = MutableLiveData<List<TransactionItem>>()
    val transactions: LiveData<List<TransactionItem>> = _transactions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchWallets() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("HomeViewModel", "Fetching wallets...")
                val wallets = walletRepository.getWallets()

                if (wallets.isNotEmpty()) {
                    _wallets.value = wallets
                    // FIX: Calculate total balance after data received
                    calculateTotalBalance()
                    Log.d("HomeViewModel", "Wallets fetched: ${wallets.size}")
                } else {
                    _error.value = "No wallets found"
                    _totalBalance.value = 0
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching wallets", e)
                _error.value = "Failed to fetch wallets: ${e.message}"
                _totalBalance.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTransactions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("HomeViewModel", "Fetching transactions...")
                val transactions = transactionRepository.getTransactions()

                if (transactions.isNotEmpty()) {
                    _transactions.value = transactions
                    // FIX: Calculate income/outcome after data received
                    calculateIncomeOutcome()
                    Log.d("HomeViewModel", "Transactions fetched: ${transactions.size}")
                } else {
                    _error.value = "No transactions found"
                    _totalIncome.value = 0
                    _totalOutcome.value = 0
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching transactions", e)
                _error.value = "Failed to fetch transactions: ${e.message}"
                _totalIncome.value = 0
                _totalOutcome.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }

    // FIX: Private function untuk calculate total balance
    private fun calculateTotalBalance() {
        var total = 0
        _wallets.value?.forEach { wallet ->
            total += wallet.balance
        }
        _totalBalance.value = total
        Log.d("HomeViewModel", "Total balance calculated: $total")
    }

    // FIX: Private function untuk calculate income
    private fun calculateIncomeOutcome() {
        var totalIncome = 0
        var totalOutcome = 0
        
        _transactions.value?.forEach { transaction ->
            when (transaction.type.lowercase()) {
                "income" -> totalIncome += transaction.amount
                "outcome" -> totalOutcome += transaction.amount
            }
        }
        
        _totalIncome.value = totalIncome
        _totalOutcome.value = totalOutcome
        
        Log.d("HomeViewModel", "Income calculated: $totalIncome")
        Log.d("HomeViewModel", "Outcome calculated: $totalOutcome")
    }

    // Public functions untuk manual refresh calculations
    fun recalculateAll() {
        calculateTotalBalance()
        calculateIncomeOutcome()
    }

    fun refreshData() {
        Log.d("HomeViewModel", "Refreshing all data...")
        fetchWallets()
        fetchTransactions()
    }
}


