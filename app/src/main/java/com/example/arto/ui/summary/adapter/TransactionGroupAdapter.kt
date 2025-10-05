package com.example.arto.ui.summary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.arto.data.model.TransactionItem
import com.example.arto.databinding.TransactionGroupLayoutBinding
import com.example.arto.ui.common.utils.FormatCurenrency
import com.example.arto.ui.home.adapter.TransactionAdapter

data class TransactionGroup(
    val date: String,
    val displayDate: String,
    val transactions: List<TransactionItem>,
    val totalAmount: Int
)

class TransactionGroupAdapter(
    private val onItemClick: (TransactionItem) -> Unit
) : androidx.recyclerview.widget.ListAdapter<TransactionGroup, TransactionGroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = TransactionGroupLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GroupViewHolder(
        private val binding: TransactionGroupLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(group: TransactionGroup) {
            with(binding) {
                // Set group date
                tvGroupDate.text = group.displayDate

                // Set group total
                val formattedTotal = FormatCurenrency.format(group.totalAmount.toInt())
                tvGroupTotal.text = if (group.totalAmount >= 0) {
                    "+$formattedTotal"
                } else {
                    formattedTotal
                }

                // Set color based on total
                if (group.totalAmount >= 0) {
                    tvGroupTotal.setTextColor(
                        binding.root.context.getColor(android.R.color.holo_green_dark)
                    )
                } else {
                    tvGroupTotal.setTextColor(
                        binding.root.context.getColor(com.example.arto.R.color.md_theme_error)
                    )
                }

                // Setup nested RecyclerView for transactions
                val transactionAdapter = TransactionAdapter(onItemClick)
                rvGroupTransactions.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = transactionAdapter
                    isNestedScrollingEnabled = false
                }

                transactionAdapter.submitList(group.transactions)
            }
        }
    }

    class GroupDiffCallback : DiffUtil.ItemCallback<TransactionGroup>() {
        override fun areItemsTheSame(oldItem: TransactionGroup, newItem: TransactionGroup): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: TransactionGroup, newItem: TransactionGroup): Boolean {
            return oldItem == newItem
        }
    }
}