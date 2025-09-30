package com.example.arto.data.network

import com.example.arto.data.model.TransactionItem
import com.example.arto.data.model.WalletItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("wallets")
    suspend fun getWallets(): Response<List<WalletItem>>

    @GET("transactions")
    suspend fun getTransactions(): Response<List<TransactionItem>>
}