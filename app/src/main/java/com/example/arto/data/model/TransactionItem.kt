package com.example.arto.data.model

import com.google.gson.annotations.SerializedName


data class TransactionItem (
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("title")
    val title: String,

    @SerializedName("amount")
    val amount: Int = 0,

    @SerializedName("type")
    val type: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("user_id")
    val user_id: Int = 0,

    @SerializedName("wallet_id")
    val wallet_id: Int = 0,

    @SerializedName("created_at")
    val created_at: String,
)