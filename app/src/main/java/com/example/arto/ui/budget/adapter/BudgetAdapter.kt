package com.example.arto.ui.budget.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.arto.data.model.BudgetItem
import com.example.arto.databinding.BudgetLayoutBinding
import com.example.arto.ui.common.utils.FormatCurenrency
import java.text.SimpleDateFormat
import java.util.Locale

class BudgetAdapter(
    private val onItemClick: (BudgetItem) -> Unit
) : ListAdapter<BudgetItem, BudgetAdapter.BudgetViewHolder>(BudgetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = BudgetLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BudgetViewHolder(
        private val binding: BudgetLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(budget: BudgetItem) {
            with(binding) {
                // Budget title
                budgetTitle.text = budget.title

                // Category with hashtag
                categoryTitle.text = "#${budget.category_name}"

                // Format date range
                val dateRange = formatDateRange(budget.date_start, budget.date_end)
                dateRangeText.text = dateRange

                // Format amounts - CORRECTED LOGIC
                val spentAmount = FormatCurenrency.format(budget.amount) // Amount spent
                val LimitAmount = FormatCurenrency.format(budget.limit_amount) // Total budget

                // Update amount TextViews
                tvCurrentAmount.text = spentAmount   // Show spent amount
                tvLimitAmount.text = LimitAmount    // Show total budget

                // Calculate and set progress - CORRECTED FORMULA
                val progress = if (budget.limit_amount > 0) {
                    ((budget.amount.toFloat() / budget.limit_amount.toFloat()) * 100).toInt().coerceIn(0, 100)
                } else 0

                progressBudget.setProgress(progress)

                // Click listener
                root.setOnClickListener {
                    onItemClick(budget)
                }
            }
        }

        private fun formatDateRange(startDate: String, endDate: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

                val start = inputFormat.parse(startDate)
                val end = inputFormat.parse(endDate)

                val startFormatted = start?.let { outputFormat.format(it) } ?: startDate
                val endFormatted = end?.let { outputFormat.format(it) } ?: endDate

                "$startFormatted - $endFormatted"
            } catch (e: Exception) {
                "$startDate - $endDate"
            }
        }
    }

    class BudgetDiffCallback : DiffUtil.ItemCallback<BudgetItem>() {
        override fun areItemsTheSame(oldItem: BudgetItem, newItem: BudgetItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BudgetItem, newItem: BudgetItem): Boolean {
            return oldItem == newItem
        }
    }
}