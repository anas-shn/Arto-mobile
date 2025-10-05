package com.example.arto.ui.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.arto.databinding.TransactionLayoutBinding
import androidx.recyclerview.widget.ListAdapter
import com.example.arto.data.model.TransactionItem
import com.example.arto.ui.common.utils.FormatCurenrency
import com.example.arto.ui.common.utils.FormatDate

class TransactionAdapter(
    private val onItemClick: (TransactionItem) -> Unit
) : ListAdapter<TransactionItem, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        val binding = TransactionLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: TransactionLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionItem) {
            with(binding) {


                tvTransactionTitle.text = transaction.title ?: ""
                // Format amount
                val formattedAmount = FormatCurenrency.format(transaction.amount)
                tvTransactionAmount.text = if (transaction.type.equals("income", ignoreCase = true)) {
                    "+$formattedAmount"
                } else {
                    "-$formattedAmount"
                }

                // Set color based on type
                if (transaction.type.equals("income", ignoreCase = true)) {
                    ivTypeTranscation.setImageResource(getIconByType(transaction.type))
                    // Set green color for income
                    ivTypeTranscation.setColorFilter(
                        binding.root.context.getColor(android.R.color.holo_green_dark),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    tvTransactionAmount.setTextColor(
                        binding.root.context.getColor(android.R.color.holo_green_dark)
                    )
                } else {
                    ivTypeTranscation.setImageResource(getIconByType(transaction.type))
                    // Set red color for outcome
                    ivTypeTranscation.setColorFilter(
                        binding.root.context.getColor(android.R.color.holo_red_dark),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    tvTransactionAmount.setTextColor(
                        binding.root.context.getColor(android.R.color.holo_red_dark)
                    )
                }
                tvTransactionDate.text = FormatDate.dateDMY(transaction.created_at)
            }
        }

    }

    fun getIconByType(type: String): Int {
        return when (type.lowercase()) {
            "income" -> com.example.arto.R.drawable.ic_income_24
            "outcome" -> com.example.arto.R.drawable.ic_outcome_24
            else -> com.example.arto.R.drawable.ic_money_24
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<TransactionItem>() {
        override fun areItemsTheSame(
            oldItem: TransactionItem,
            newItem: TransactionItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TransactionItem,
            newItem: TransactionItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}