package com.example.arto.data.repository

import com.example.arto.data.model.WalletItem
import com.example.arto.data.network.InstanceRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WalletRepository {

    private val apiService = InstanceRetrofit.api

    suspend fun getWallets(): List<WalletItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getWallets()

            if (response.isSuccessful) {
                val wallets = response.body() ?: emptyList()
                return@withContext wallets
            } else {
                return@withContext emptyList()
            }
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }
}