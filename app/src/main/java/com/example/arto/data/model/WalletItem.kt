package com.example.arto.data.model

import com.google.gson.annotations.SerializedName

data class WalletItem(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String,

    @SerializedName("balance")
    val balance: Int,

    @SerializedName("user_id")
    val user_id: Int,

    @SerializedName("created_at")
    val created_at: String? = null,

    @SerializedName("type")
    val type: String,

    @SerializedName("rekening")
    val rekening: Long
)