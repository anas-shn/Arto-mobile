package com.example.arto.data.repository

import android.util.Log
import com.example.arto.data.model.TransactionItem
import com.example.arto.data.network.InstanceRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRepository {
    private val apiService = InstanceRetrofit.api

    suspend fun getTransactions(): List<TransactionItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTransactions()

            if (response.isSuccessful) {
                val transactions = response.body() ?: emptyList()
                Log.d("TransactionRepository", "Successfully fetched ${transactions.size} transactions")
                return@withContext transactions
            } else {
                Log.e("TransactionRepository", "Failed to fetch transactions: ${response.code()}")
                return@withContext emptyList()
            }
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }
}