package com.example.arto.ui.home.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.arto.data.local.SessionManager
import com.example.arto.data.model.TransactionItem
import com.example.arto.data.model.WalletItem
import com.example.arto.data.repository.TransactionRepository
import com.example.arto.data.repository.WalletRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val walletRepository = WalletRepository()
    private val transactionRepository = TransactionRepository()
    private val sessionManager = SessionManager(application)

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome Back,"
    }
    val text: LiveData<String> = _text

    // Ambil nama dari SessionManager
    private val _name = MutableLiveData<String>().apply {
        value = sessionManager.getUserName() ?: "User"
    }
    val name: LiveData<String> = _name

    // Ambil user ID dari SessionManager
    private val userId: Int
        get() = sessionManager.getUserId()

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

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        // Load user info saat ViewModel dibuat
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val userName = sessionManager.getUserName()
        val userEmail = sessionManager.getUserEmail()
        val userId = sessionManager.getUserId()

        _name.value = userName ?: "User"
    }

    fun fetchWallets() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val wallets = walletRepository.getWallets()

                if (wallets.isNotEmpty()) {
                    _wallets.value = wallets
                    calculateTotalBalance()
                } else {
                    _error.value = "No wallets found"
                    _totalBalance.value = 0
                }

            } catch (e: Exception) {
                _error.value = "Failed to fetch wallets: ${e.message}"
                _totalBalance.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createWallet(
        name: String,
        balance: Int,
        type: String,
        rekening: Long
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val wallet = WalletItem(
                    name = name,
                    balance = balance,
                    type = type,
                    rekening = rekening,
                    user_id = userId
                )

                walletRepository.createWallet(wallet)
                fetchWallets()

            } catch (e: Exception) {
                _error.value = "Failed to create wallet: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateWallet(
        id: Int,
        name: String,
        balance: Int,
        type: String,
        rekening: Long
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val wallet = WalletItem(
                    id = id,
                    name = name,
                    balance = balance,
                    type = type,
                    rekening = rekening,
                    user_id = userId
                )

                Log.d(TAG, "Updating wallet ID: $id")
                walletRepository.updateWallet(id, wallet)
                fetchWallets()

            } catch (e: Exception) {
                Log.e(TAG, "Error updating wallet", e)
                _error.value = "Failed to update wallet: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteWallet(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d(TAG, "Deleting wallet ID: $id")
                val success = walletRepository.deleteWallet(id)
                if (success) {
                    fetchWallets()
                } else {
                    _error.value = "Failed to delete wallet"
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error deleting wallet", e)
                _error.value = "Failed to delete wallet: ${e.message}"
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

                Log.d(TAG, "Fetching transactions for user ID: $userId")
                val transactions = transactionRepository.getTransactions()

                if (transactions.isNotEmpty()) {
                    _transactions.value = transactions
                    calculateIncomeOutcome()
                    Log.d(TAG, "Transactions fetched: ${transactions.size}")
                } else {
                    Log.d(TAG, "No transactions found")
                    _error.value = "No transactions found"
                    _totalIncome.value = 0
                    _totalOutcome.value = 0
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching transactions", e)
                _error.value = "Failed to fetch transactions: ${e.message}"
                _totalIncome.value = 0
                _totalOutcome.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculateTotalBalance() {
        var total = 0
        _wallets.value?.forEach { wallet ->
            total += wallet.balance
        }
        _totalBalance.value = total
    }

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
        Log.d(TAG, "Income: $totalIncome, Outcome: $totalOutcome")
    }

    fun recalculateAll() {
        calculateTotalBalance()
        calculateIncomeOutcome()
    }

    fun clearError() {
        _error.value = null
    }

    fun refreshData() {
        Log.d(TAG, "Refreshing all data for user ID: $userId")
        fetchWallets()
        fetchTransactions()
    }

    // Function untuk update nama user (jika dibutuhkan)
    fun updateUserName(newName: String) {
        _name.value = newName
    }
}