package com.example.arto.ui.budget.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arto.data.model.BudgetItem
import com.example.arto.databinding.BudgetModalBinding
import com.example.arto.databinding.FragmentBudgetBinding
import com.example.arto.ui.budget.viewmodel.BudgetViewModel
import com.example.arto.ui.budget.adapter.BudgetAdapter
import com.example.arto.ui.budget.helper.BudgetBottomSheetHelper
import com.example.arto.ui.budget.helper.BudgetFormData
import com.example.arto.ui.common.utils.FormatCurenrency
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var budgetAdapter: BudgetAdapter
    private lateinit var budgetBottomSheetHelper: BudgetBottomSheetHelper
    private var editingBudget: BudgetItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        budgetViewModel = ViewModelProvider(this)[BudgetViewModel::class.java]
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)

        setupBottomSheetHelper()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        budgetViewModel.fetchBudgets()
    }

    private fun setupBottomSheetHelper() {
        // FIX: Use new BudgetBottomSheetHelper
        budgetBottomSheetHelper = BudgetBottomSheetHelper(this)

        // Setup listeners using new functional approach
        budgetBottomSheetHelper.setOnSaveListener { data ->
            handleBudgetSubmit(data)
        }

        budgetBottomSheetHelper.setOnCancelListener {
            editingBudget = null
        }
    }

    private fun setupRecyclerView() {
        budgetAdapter = BudgetAdapter { budgetItem ->
            showBudgetActionPopup(budgetItem)
        }

        binding.budgetRecyclerView.apply {
            adapter = budgetAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupObservers() {
        // Loading state
        budgetViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading indicator
            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        // Budget data
        budgetViewModel.budgets.observe(viewLifecycleOwner) { budgets ->
            budgetAdapter.submitList(budgets)
        }

        // Create success
        budgetViewModel.createSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Budget berhasil dibuat!", Toast.LENGTH_SHORT).show()
                budgetViewModel.clearCreateSuccess()
            }
        }

        // Update success
        budgetViewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Budget berhasil diupdate!", Toast.LENGTH_SHORT).show()
                budgetViewModel.clearUpdateSuccess()
                editingBudget = null
            }
        }

        // Delete success
        budgetViewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Budget berhasil dihapus!", Toast.LENGTH_SHORT).show()
                budgetViewModel.clearDeleteSuccess()
            }
        }

        // Error handling
        budgetViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                budgetViewModel.clearError()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnAddBudget.setOnClickListener {
            editingBudget = null
            // FIX: Use new helper method
            budgetBottomSheetHelper.showBottomSheet()
        }
    }

    private fun showBudgetActionPopup(budget: BudgetItem) {
        val popupBinding = BudgetModalBinding.inflate(layoutInflater)

        // Populate popup with budget data
        with(popupBinding) {
            tvBudgetTitle.text = budget.title
            tvCategoryTitle.text = "#${budget.category_name}"
            tvCurrentAmount.text = FormatCurenrency.format(budget.amount)
            tvLimitAmount.text = FormatCurenrency.format(budget.limit_amount)
            tvDateRange.text = formatDateRange(budget.date_start, budget.date_end)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(popupBinding.root)
            .create()

        // Setup button listeners
        popupBinding.btnEditBudget.setOnClickListener {
            dialog.dismiss()
            editBudget(budget)
        }

        popupBinding.btnDeleteBudget.setOnClickListener {
            dialog.dismiss()
            confirmDeleteBudget(budget)
        }

        dialog.show()
    }

    private fun editBudget(budget: BudgetItem) {
        editingBudget = budget

        // FIX: Create edit data map with ID for update mode
        val editData = mapOf(
            "id" to budget.id,
            "title" to budget.title,
            "limit_amount" to budget.limit_amount,
            "category_name" to budget.category_name,
            "date_start" to budget.date_start,
            "date_end" to budget.date_end
        )

        // FIX: Use new helper method
        budgetBottomSheetHelper.showBottomSheet(editData)
    }

    private fun confirmDeleteBudget(budget: BudgetItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Budget")
            .setMessage("Apakah Anda yakin ingin menghapus budget '${budget.title}'?")
            .setPositiveButton("Hapus") { _, _ ->
                budgetViewModel.deleteBudget(budget.id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // FIX: Update method parameter to use BudgetFormData
    private fun handleBudgetSubmit(data: BudgetFormData) {
        if (data.id != null) {
            // Update existing budget
            budgetViewModel.updateBudget(
                data.id,
                data.title,
                data.limit_amount,
                data.dateStart,
                data.dateEnd,
                data.categoryName
            )
        } else {
            // Create new budget
            budgetViewModel.createBudget(
                data.title,
                data.limit_amount,
                data.dateStart,
                data.dateEnd,
                data.categoryName
            )
        }
    }

    private fun formatDateRange(startDate: String, endDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

            val start = inputFormat.parse(startDate)
            val end = inputFormat.parse(endDate)

            val startFormatted = start?.let { outputFormat.format(it) } ?: startDate
            val endFormatted = end?.let { outputFormat.format(it) } ?: endDate

            "$startFormatted - $endFormatted"
        } catch (e: Exception) {
            "$startDate - $endDate"
        }
    }

    private fun progresPercent() {
        // TODO: Implement progress percentage calculation if needed
    }

    private fun showLoading() {
        binding.budgetLoading.visibility = View.VISIBLE
        binding.budgetParent.alpha = 0.5f
        // disable interaction
        binding.btnAddBudget.isEnabled = false
    }

    private fun hideLoading() {
        binding.budgetLoading.visibility = View.GONE
        binding.budgetParent.alpha = 1.0f
        // enable interaction
        binding.btnAddBudget.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}