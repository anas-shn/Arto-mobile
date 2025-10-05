package com.example.arto.ui.transaction.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.arto.R
import com.example.arto.data.model.BudgetItem
import com.example.arto.databinding.FormTransactionBinding
import com.example.arto.databinding.FragmentTransactionBinding
import com.example.arto.ui.transaction.adapter.WalletSelectionAdapter
import com.example.arto.ui.transaction.viewmodel.TransactionViewModel
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var formBinding: FormTransactionBinding
    private lateinit var walletAdapter: WalletSelectionAdapter
    private lateinit var transactionViewModel: TransactionViewModel

    private var selectedWalletId = 0
    private var selectedDate = ""
    private var selectedTransactionType = "Outcome"

    companion object {
        private const val TAG = "TransactionFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        formBinding = binding.includeTransactionForm

        setupViews()
        setupObservers()

        return binding.root
    }

    private fun setupViews() {
        setupToolbar()
        setupTransactionForm()
        setupSaveButton()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupTransactionForm() {
        setupTransactionTypeTabs()
        setupDatePicker()
        setupWalletRecycler()
        setCurrentDate()
    }

    private fun setupSaveButton() {
        binding.btnSimpan.setOnClickListener {
            handleSubmit()
        }
    }

    private fun setupObservers() {
        // Observe budgets for category dropdown
        transactionViewModel.budgets.observe(viewLifecycleOwner) { budgets ->
            setupCategoryDropdown(budgets)
        }

        // Observe wallets for wallet selection
        transactionViewModel.wallets.observe(viewLifecycleOwner) { wallets ->
            walletAdapter.updateWallets(wallets)
        }

        // Observe loading state
        transactionViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSimpan.isEnabled = !isLoading
            binding.btnSimpan.text = if (isLoading) {
                getString(R.string.saving)
            } else {
                getString(R.string.save)
            }
        }

        // Observe create success
        transactionViewModel.createSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    context,
                    getString(R.string.transaction_saved),
                    Toast.LENGTH_SHORT
                ).show()
                transactionViewModel.clearCreateSuccess()
                resetForm()
            }
        }

        // Observe errors
        transactionViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                transactionViewModel.clearError()
            }
        }
    }

    private fun setupTransactionTypeTabs() {
        with(formBinding.tabLayout) {
            removeAllTabs()
            addTab(newTab().setText(getString(R.string.income_transaction)))
            addTab(newTab().setText(getString(R.string.outcome_transaction)), true)

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    selectedTransactionType = if (tab?.position == 0) "Income" else "Outcome"
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun setupCategoryDropdown(budgets: List<BudgetItem>) {
        val categories = mutableListOf("Tidak ada")
        categories.addAll(
            budgets.mapNotNull {
                it.category_name.takeIf { name -> name.isNotBlank() }
            }.distinct()
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        )
        formBinding.etTransactionCategory.setAdapter(adapter)
        formBinding.etTransactionCategory.setText(categories.first(), false)
    }

    private fun setupWalletRecycler() {
        walletAdapter = WalletSelectionAdapter(emptyList()) { wallet ->
            selectedWalletId = wallet.id
            clearWalletError()
        }

        formBinding.rvWallets.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = walletAdapter
        }
    }

    private fun setupDatePicker() {
        val showPicker = { showDatePickerDialog() }
        formBinding.etTransactionDate.setOnClickListener { showPicker() }
        formBinding.tilTransactionDate.setEndIconOnClickListener { showPicker() }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                updateSelectedDate(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateSelectedDate(calendar: Calendar) {
        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        selectedDate = apiFormat.format(calendar.time)
        formBinding.etTransactionDate.setText(displayFormat.format(calendar.time))
    }

    private fun setCurrentDate() {
        val calendar = Calendar.getInstance()
        updateSelectedDate(calendar)
    }

    private fun handleSubmit() {
        // Clear previous errors
        clearAllErrors()

        // Get form values
        val title = formBinding.tfTransactionTitle.text?.toString()?.trim().orEmpty()
        val amountText = formBinding.etTransactionAmount.text?.toString()?.trim().orEmpty()
        val category = formBinding.etTransactionCategory.text?.toString()?.trim().orEmpty()

        // Validate form
        if (!validateForm(title, amountText)) {
            return
        }

        val amount = amountText.toInt()

        // Validate wallet balance for Outcome transactions
        if (selectedTransactionType == "Outcome") {
            if (!validateWalletBalance(amount)) {
                return
            }
        }

        // Submit transaction
        transactionViewModel.createTransaction(
            title = title,
            amount = amount,
            type = selectedTransactionType,
            categoryName = category,
            walletId = selectedWalletId,
            date = selectedDate
        )
    }

    private fun validateForm(title: String, amountText: String): Boolean {
        var isValid = true

        // Validate title
        if (title.isBlank()) {
            formBinding.tilTransactionTitle.error = getString(R.string.error_title_required)
            isValid = false
        }

        // Validate amount
        val amount = amountText.toIntOrNull()
        if (amount == null || amount <= 0) {
            formBinding.tfTransactionAmount.error = getString(R.string.error_amount_invalid)
            isValid = false
        }

        // Validate wallet selection
        if (selectedWalletId == 0) {
            Toast.makeText(
                context,
                getString(R.string.error_wallet_required),
                Toast.LENGTH_SHORT
            ).show()
            isValid = false
        }

        return isValid
    }

    private fun validateWalletBalance(amount: Int): Boolean {
        val wallet = transactionViewModel.getWalletById(selectedWalletId)

        if (wallet == null) {
            Toast.makeText(
                context,
                getString(R.string.error_wallet_not_found),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (amount > wallet.balance) {
            formBinding.tfTransactionAmount.error = getString(
                R.string.error_insufficient_balance,
                wallet.balance
            )
            Toast.makeText(
                context,
                getString(R.string.error_insufficient_balance, wallet.balance),
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        return true
    }

    private fun resetForm() {
        // Reset input fields
        formBinding.tfTransactionTitle.text?.clear()
        formBinding.etTransactionAmount.text?.clear()
        formBinding.etTransactionCategory.setText("Tidak ada", false)

        // Reset wallet selection
        selectedWalletId = 0
        walletAdapter.clearSelection()

        // Reset to current date
        setCurrentDate()

        // Reset to Outcome tab
        formBinding.tabLayout.selectTab(formBinding.tabLayout.getTabAt(1))
        selectedTransactionType = "Outcome"

        // Clear all errors
        clearAllErrors()

        Log.d(TAG, "Form reset successfully")
    }

    private fun clearAllErrors() {
        formBinding.tilTransactionTitle.error = null
        formBinding.tfTransactionAmount.error = null
    }

    private fun clearWalletError() {
        // Clear wallet related errors when wallet is selected
        formBinding.tfTransactionAmount.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}