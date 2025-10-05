package com.example.arto.data.model

import com.google.gson.annotations.SerializedName

data class BudgetItem(

    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("title")
    val title: String = "",

    @SerializedName("amount")
    val amount: Int = 0,

    @SerializedName("limit_amount")
    val limit_amount: Int = 0,

    @SerializedName("category_name")
    val category_name: String = "",

    @SerializedName("user_id")
    val user_id: Int = 1,

    @SerializedName("date_start")
    val date_start: String = "",

    @SerializedName("date_end")
    val date_end: String = ""
)