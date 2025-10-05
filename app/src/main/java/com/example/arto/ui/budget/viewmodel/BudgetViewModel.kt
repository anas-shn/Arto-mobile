package com.example.arto.ui.budget.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.arto.data.model.BudgetItem
import com.example.arto.data.repository.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val budgetRepository = BudgetRepository(application)

    private val _budgets = MutableLiveData<List<BudgetItem>>()
    val budgets: LiveData<List<BudgetItem>> = _budgets

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _createSuccess = MutableLiveData<Boolean>()
    val createSuccess: LiveData<Boolean> = _createSuccess

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

    companion object {
        private const val TAG = "BudgetViewModel"
    }

    init {
        fetchBudgets()
    }

    fun fetchBudgets() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val budgets = budgetRepository.getBudgets()
                _budgets.value = budgets


            } catch (e: Exception) {
                _error.value = "Gagal Mendapatakan budgets: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createBudget(
        title: String,
        limit_amount: Int,
        dateStart: String,
        dateEnd: String,
        categoryName: String
    ) {
        viewModelScope.launch {
            try {

                _isLoading.value = true
                _error.value = null

                val budget = BudgetItem(
                    title = title,
                    limit_amount = limit_amount,
                    amount = 0, // Initial amount is 0
                    category_name = categoryName,
                    date_start = dateStart,
                    date_end = dateEnd,
                    user_id = 1
                )


                val created = budgetRepository.createBudget(budget)
                fetchBudgets()
                _createSuccess.value = true

                Log.d(TAG, "=== createBudget SUCCESS ===")

            } catch (e: Exception) {
                _error.value = "Failed to create budget: ${e.message}"
                _createSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBudget(
        id: Int,
        title: String,
        limit_amount: Int,
        dateStart: String,
        dateEnd: String,
        categoryName: String
    ) {
        viewModelScope.launch {
            try {

                _isLoading.value = true
                _error.value = null

                // Get current budget to preserve amount
                val currentBudgets = _budgets.value ?: emptyList()
                val currentBudget = currentBudgets.find { it.id == id }
                val currentAmount = currentBudget?.amount ?: 0

                val budget = BudgetItem(
                    id = id,
                    title = title,
                    limit_amount = limit_amount,
                    amount = currentAmount, // Preserve current amount
                    category_name = categoryName,
                    date_start = dateStart,
                    date_end = dateEnd,
                    user_id = 1
                )

                budgetRepository.updateBudget(id, budget)
                fetchBudgets()
                _updateSuccess.value = true

            } catch (e: Exception) {
                _error.value = "Gagal memperbarui budget: ${e.message}"
                _updateSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBudget(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val success = budgetRepository.deleteBudget(id)
                if (success) {
                    fetchBudgets()
                    _deleteSuccess.value = true
                } else {
                    _error.value = "Failed to delete budget"
                    _deleteSuccess.value = false
                }

            } catch (e: Exception) {
                _error.value = "Failed to delete budget: ${e.message}"
                _deleteSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshData() {
        fetchBudgets()
    }

    fun clearError() {
        _error.value = null
    }

    fun clearCreateSuccess() {
        _createSuccess.value = false
    }

    fun clearUpdateSuccess() {
        _updateSuccess.value = false
    }

    fun clearDeleteSuccess() {
        _deleteSuccess.value = false
    }
}