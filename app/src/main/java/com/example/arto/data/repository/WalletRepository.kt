package com.example.arto.data.repository

import com.example.arto.data.model.WalletItem
import com.example.arto.data.network.InstanceRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WalletRepository() {

    private val apiService = InstanceRetrofit.api

    // GET all wallets
    suspend fun getWallets(): List<WalletItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getWallets()
            if (response.isSuccessful) {
                return@withContext response.body() ?: emptyList()
            } else {
                throw Exception("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Network Error: ${e.message}")
        }
    }

    // GET single wallet by ID
    suspend fun getWalletById(id: Int): WalletItem = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getWalletById(id)
            if (response.isSuccessful) {
                return@withContext response.body() ?: throw Exception("Wallet not found")
            } else {
                throw Exception("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Network Error: ${e.message}")
        }
    }

    // CREATE new wallet
    suspend fun createWallet(wallet: WalletItem): WalletItem = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createWallet(wallet)
            if (response.isSuccessful) {
                return@withContext response.body() ?: throw Exception("Failed to create wallet")
            } else {
                throw Exception("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Network Error: ${e.message}")
        }
    }

    // UPDATE existing wallet
    suspend fun updateWallet(id: Int, wallet: WalletItem): WalletItem = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateWallet(id, wallet)
            if (response.isSuccessful) {
                return@withContext response.body() ?: throw Exception("Failed to update wallet")
            } else {
                throw Exception("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Network Error: ${e.message}")
        }
    }

    // DELETE wallet
    suspend fun deleteWallet(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteWallet(id)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            throw Exception("Network Error: ${e.message}")
        }
    }
}