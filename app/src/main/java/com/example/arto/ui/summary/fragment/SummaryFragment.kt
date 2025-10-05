package com.example.arto.ui.summary.fragment

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arto.databinding.FragmentSummaryBinding
import com.example.arto.ui.common.utils.FormatCurenrency
import com.example.arto.ui.summary.viewmodel.SummaryViewModel
import com.example.arto.ui.summary.adapter.TransactionGroupAdapter
import java.util.Calendar

class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var summaryViewModel: SummaryViewModel
    private lateinit var transactionGroupAdapter: TransactionGroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        summaryViewModel = ViewModelProvider(this)[SummaryViewModel::class.java]
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupMonthFilter()
        setupObservers()

        return binding.root
    }

    private fun setupRecyclerView() {
        transactionGroupAdapter = TransactionGroupAdapter { transactionItem ->
            Toast.makeText(context, "Selected: ${transactionItem.title}", Toast.LENGTH_SHORT).show()
        }

        binding.rvTransactionSummary.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionGroupAdapter
        }
    }

    private fun setupMonthFilter() {
        val monthFilterData = summaryViewModel.getMonthFilterData()
        val monthNames = monthFilterData.map { it.first }

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_dropdown_item_1line,
            monthNames
        )

        binding.etMonthFilter.setAdapter(adapter)

        // Set current month as default
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentMonthName = monthFilterData[currentMonth].first
        binding.etMonthFilter.setText(currentMonthName, false)

        binding.etMonthFilter.setOnItemClickListener { _, _, position, _ ->
            val selectedMonth = monthFilterData[position].second
            summaryViewModel.filterByMonth(selectedMonth)
        }
    }

    private fun setupObservers() {
        // Loading state
        summaryViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Transaction groups
        summaryViewModel.transactionGroups.observe(viewLifecycleOwner) { groups ->
            transactionGroupAdapter.submitList(groups)
        }

        // Total income
        summaryViewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvTotalIncome.text = FormatCurenrency.format(income.toInt())
        }

        // Total expense
        summaryViewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            binding.tvTotalExpense.text = FormatCurenrency.format(expense.toInt())
        }

        // Error handling
        summaryViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                summaryViewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}