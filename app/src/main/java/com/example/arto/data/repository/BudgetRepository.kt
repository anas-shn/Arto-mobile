package com.example.arto.data.repository

import android.content.Context
import android.util.Log
import com.example.arto.data.model.BudgetItem
import com.example.arto.data.network.InstanceRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BudgetRepository(context: Context) {

    private val apiService = InstanceRetrofit.api

    companion object {
        private const val TAG = "BudgetRepository"
    }

    // GET all budgets
    suspend fun getBudgets(): List<BudgetItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getBudgets()

            if (response.isSuccessful) {
                val budgets = response.body() ?: emptyList()
                return@withContext budgets
            } else {
                val errorBody = response.errorBody()?.string()
                val error =
                    "API Error: ${response.code()} - ${response.message()} - Body: $errorBody"
                throw Exception(error)
            }
        } catch (e: Exception) {
            throw Exception("Network Error: ${e.message}")
        }
    }

    // GET single budget by ID
    suspend fun getBudgetById(id: Int): BudgetItem = withContext(Dispatchers.IO) {
        try {

            val response = apiService.getBudgetById(id)

            if (response.isSuccessful) {
                val budget = response.body() ?: throw Exception("Budget not found")
                return@withContext budget
            } else {
                val errorBody = response.errorBody()?.string()
                val error =
                    "API Error: ${response.code()} - ${response.message()} - Body: $errorBody"
                throw Exception(error)
            }
        } catch (e: Exception) {
            throw Exception("Network Error: ${e.message}")
        }
    }

    // CREATE new budget
    suspend fun createBudget(budget: BudgetItem): BudgetItem = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createBudget(budget)

            if (response.isSuccessful) {
                // Handle empty response body (API may return 200/201 with empty body)
                val created = try {
                    response.body()
                } catch (e: Exception) {
                    null
                }

                if (created != null) {
                    return@withContext created
                } else {
                    // API returned success but empty body - return the budget we sent
                    return@withContext budget
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val error =
                    "API Error: ${response.code()} - ${response.message()} - Body: $errorBody"
                throw Exception(error)
            }
        } catch (e: com.google.gson.JsonSyntaxException) {
            // Handle JSON parsing error (empty response)
            return@withContext budget
        } catch (e: java.io.EOFException) {
            // Handle EOF error (empty response)
            return@withContext budget
        } catch (e: Exception) {
            throw Exception("Network Error: ${e.message}")
        }
    }

    // UPDATE existing budget
    suspend fun updateBudget(id: Int, budget: BudgetItem): BudgetItem =
        withContext(Dispatchers.IO) {
            try {

                val response = apiService.updateBudget(id, budget)
                Log.d(TAG, "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val updated = response.body() ?: throw Exception("Failed to update budget")

                    return@withContext updated
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error =
                        "API Error: ${response.code()} - ${response.message()} - Body: $errorBody"
                    Log.e(TAG, error)
                    Log.e(TAG, "=== updateBudget FAILED ===")
                    throw Exception(error)
                }
            } catch (e: Exception) {
                throw Exception("Network Error: ${e.message}")
            }
        }

    // FIX: Use specific amount endpoint like Postman
    suspend fun updateBudgetAmount(id: Int, newAmount: Int): BudgetItem =
        withContext(Dispatchers.IO) {
            try {


                val amountMap = mapOf("amount" to newAmount)

                val response = apiService.updateBudgetAmount(id, amountMap)

                if (response.isSuccessful) {
                    // Handle empty response body
                    val updated = try {
                        response.body()
                    } catch (e: Exception) {
                        null
                    }
                    
                    if (updated != null) {
                        return@withContext updated
                    } else {
                        // Fetch the updated budget
                        val fetchedBudget = getBudgetById(id)
                        return@withContext fetchedBudget
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error =
                        "API Error: ${response.code()} - ${response.message()} - Body: $errorBody"
                    throw Exception(error)
                }

            } catch (e: com.google.gson.JsonSyntaxException) {
                val fetchedBudget = getBudgetById(id)
                return@withContext fetchedBudget
            } catch (e: java.io.EOFException) {
                val fetchedBudget = getBudgetById(id)
                return@withContext fetchedBudget
            } catch (e: Exception) {
                throw Exception("Failed to update budget amount: ${e.message}")
            }
        }

    // DELETE budget
    suspend fun deleteBudget(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {

            val response = apiService.deleteBudget(id)
            val success = response.isSuccessful

            return@withContext success
        } catch (e: Exception) {
            throw Exception("Network Error: ${e.message}")
        }
    }
}