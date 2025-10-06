package com.example.arto.data.network

import com.example.arto.data.model.AuthResponse
import com.example.arto.data.model.BudgetItem
import com.example.arto.data.model.LoginRequest
import com.example.arto.data.model.LoginResponse
import com.example.arto.data.model.RegisterRequest
import com.example.arto.data.model.TransactionItem
import com.example.arto.data.model.WalletItem
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("wallets")
    suspend fun getWallets(): Response<List<WalletItem>>

    // Wallet Endpoints
    @GET("wallets/{id}")
    suspend fun getWalletById(@Path("id") id: Int): Response<WalletItem>

    @POST("wallets")
    suspend fun createWallet(@Body wallet: WalletItem): Response<WalletItem>

    @PUT("203c3219-5089-405b-8704-3718f7158220/wallets/{id}")
    suspend fun updateWallet(@Path("id") id: Int, @Body wallet: WalletItem): Response<WalletItem>

    @DELETE("203c3219-5089-405b-8704-3718f7158220/wallets/{id}")
    suspend fun deleteWallet(@Path("id") id: Int): Response<Unit>

    // Budget endpoints
    @GET("budgets")
    suspend fun getBudgets(): Response<List<BudgetItem>>

    @GET("budgets/{id}")
    suspend fun getBudgetById(@Path("id") id: Int): Response<BudgetItem>

    @POST("budgets")
    suspend fun createBudget(@Body budget: BudgetItem): Response<BudgetItem>

    @PUT("203c3219-5089-405b-8704-3718f7158220/budgets/{id}")
    suspend fun updateBudget(@Path("id") id: Int, @Body budget: BudgetItem): Response<BudgetItem>

    @PUT("899eae30-6f34-4b43-a671-4215dc97fecc/budgets/{id}/amount")
    suspend fun updateBudgetAmount(
        @Path("id") id: Int,
        @Body amount: Map<String, Int>
    ): Response<BudgetItem>

    @DELETE("203c3219-5089-405b-8704-3718f7158220/budgets/{id}")
    suspend fun deleteBudget(@Path("id") id: Int): Response<Unit>

    // Transaction endpoints

    @GET("transactions")
    suspend fun getTransactions(): Response<List<TransactionItem>>

    @POST("transactions")
    suspend fun createTransaction(@Body transaction: TransactionItem): Response<TransactionItem>

        // === AUTH ===
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<List<LoginResponse>>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>


}