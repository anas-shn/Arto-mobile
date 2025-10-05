package com.example.arto.ui.transaction.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.arto.data.model.BudgetItem
import com.example.arto.data.model.TransactionItem
import com.example.arto.data.model.WalletItem
import com.example.arto.data.repository.BudgetRepository
import com.example.arto.data.repository.TransactionRepository
import com.example.arto.data.repository.WalletRepository
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionRepository = TransactionRepository()
    private val budgetRepository = BudgetRepository(application)
    private val walletRepository = WalletRepository()

    private val _budgets = MutableLiveData<List<BudgetItem>>()
    val budgets: LiveData<List<BudgetItem>> = _budgets

    private val _wallets = MutableLiveData<List<WalletItem>>()
    val wallets: LiveData<List<WalletItem>> = _wallets

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _createSuccess = MutableLiveData<Boolean>()
    val createSuccess: LiveData<Boolean> = _createSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _selectedImageUri = MutableLiveData<String?>()
    val selectedImageUri: LiveData<String?> = _selectedImageUri

    init {
        fetchBudgets()
        fetchWallets()
    }

    fun fetchBudgets() {
        viewModelScope.launch {
            try {
                val budgetList = budgetRepository.getBudgets()
                _budgets.value = budgetList
            } catch (e: Exception) {
                _error.value = "Failed to fetch budgets: ${e.message}"
            }
        }
    }

    fun fetchWallets() {
        viewModelScope.launch {
            try {
                val walletList = walletRepository.getWallets()
                _wallets.value = walletList
            } catch (e: Exception) {
                _error.value = "Failed to fetch wallets: ${e.message}"
            }
        }
    }

    fun getWalletById(walletId: Int): WalletItem? {
        return _wallets.value?.find { it.id == walletId }
    }

    fun createTransaction(
        title: String,
        amount: Int,
        type: String,
        categoryName: String,
        walletId: Int,
        date: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val transaction = TransactionItem(
                    title = title,
                    amount = amount,
                    type = type,
                    category_name = categoryName.takeIf { it != "Tidak ada" } ?: "",
                    wallet_id = walletId,
                    created_at = date,
                    user_id = 1
                )

                // Create transaction
                val success = transactionRepository.createTransaction(transaction)

                if (success) {
                    // Update budget amount if category selected (only for Outcome)
                    if (type == "Outcome" && categoryName.isNotBlank() && categoryName != "Tidak ada") {
                        updateBudgetAmount(categoryName, amount)
                    }

                    // Update wallet balance
                    updateWalletBalance(walletId, amount, type)

                    _createSuccess.value = true
                } else {
                    _error.value = "Failed to create transaction"
                    _createSuccess.value = false
                }

            } catch (e: Exception) {
                _error.value = "Failed to create transaction: ${e.message}"
                _createSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

        private suspend fun updateBudgetAmount(categoryName: String, amount: Int) {
        try {

            val budgets = _budgets.value

            if (budgets == null || budgets.isEmpty()) {
                return
            }

            val matchingBudget = budgets.find {
                it.category_name.equals(categoryName, ignoreCase = true)
            }

            if (matchingBudget == null) {
                return
            }

            // Calculate new amount (spent amount increases)
            val newAmount = matchingBudget.amount + amount

            // Check if exceeds balance
            if (newAmount > matchingBudget.limit_amount) {
                _error.value = "Peringatan: Budget melebihi batas limit"
                return
            }

            // Update budget
            budgetRepository.updateBudgetAmount(matchingBudget.id, newAmount)

            // Refresh budgets list
            fetchBudgets()

        } catch (e: Exception) {
            // Don't fail transaction, just log warning
            _error.value = "Peringatan: Budget tidak dapat diperbarui - ${e.message}"
        }
    }

    private suspend fun updateWalletBalance(walletId: Int, amount: Int, type: String) {
        try {
            val wallets = _wallets.value ?: return
            val wallet = wallets.find { it.id == walletId } ?: return

            // Calculate new balance
            val newBalance = if (type == "Income") {
                wallet.balance + amount
            } else {
                wallet.balance - amount
            }

            // Create updated wallet object
            val updatedWallet = WalletItem(
                id = wallet.id,
                name = wallet.name,
                type = wallet.type,
                balance = newBalance, // Updated balance
                rekening = wallet.rekening,
                user_id = wallet.user_id,
                created_at = wallet.created_at,

            )

            // Update wallet in repository
            walletRepository.updateWallet(walletId, updatedWallet)

            // Update local wallet list
            val updatedWallets = wallets.map {
                if (it.id == walletId) updatedWallet else it
            }
            _wallets.value = updatedWallets

        } catch (e: Exception) {
            // Log error but don't fail the transaction
            _error.value = "Warning: Wallet balance not updated - ${e.message}"
        }
    }

    fun setImageUri(uri: String?) {
        _selectedImageUri.value = uri
    }

    fun clearCreateSuccess() {
        _createSuccess.value = false
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        private const val TAG = "TransactionViewModel"
    }
}