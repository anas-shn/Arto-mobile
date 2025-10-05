package com.example.arto.ui.budget.helper

import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.arto.R
import com.example.arto.ui.common.bottomsheet.BaseBottomSheetHelper
import com.example.arto.ui.common.bottomsheet.DatePickerHelper
import com.google.android.material.textfield.TextInputLayout

data class BudgetFormData(
    val id: Int? = null,
    val title: String,
    val limit_amount: Int,
    val dateStart: String,
    val dateEnd: String,
    val categoryName: String,
    val userId: Int,
    val amount: Int = 0
)

class BudgetBottomSheetHelper(fragment: Fragment) : BaseBottomSheetHelper<BudgetFormData>(fragment) {


    private var datePickerHelper: DatePickerHelper? = null
    private var selectedStartDate: String = ""
    private var selectedEndDate: String = ""

    override fun getTitle(): String {
        return if (editData != null) "Edit Budget" else "Tambah Budget"
    }

    override fun getLayoutResource(): Int = R.layout.form_budget

    override fun setupFormLogic(formView: View) {
        datePickerHelper = DatePickerHelper(fragment.requireContext())
        setupBudgetForm(formView)
    }

    override fun prefillForm(data: Map<String, Any>) {
        val formView = binding?.formContainer?.getChildAt(0) ?: return

        val titleEditText = formView.findViewById<EditText>(R.id.et_budget_name)
        val balanceEditText = formView.findViewById<EditText>(R.id.et_budget_balance)
        val categoryEditText = formView.findViewById<EditText>(R.id.et_budget_category)
        val startDateEditText = formView.findViewById<EditText>(R.id.et_start_date)
        val endDateEditText = formView.findViewById<EditText>(R.id.et_end_date)

        titleEditText?.setText(data["title"] as? String ?: "")
        balanceEditText?.setText((data["limit_amount"] as? Int)?.toString() ?: "")
        categoryEditText?.setText(data["category_name"] as? String ?: "")

        // Format and set dates
        val startDate = data["date_start"] as? String ?: ""
        val endDate = data["date_end"] as? String ?: ""

        if (startDate.isNotEmpty()) {
            selectedStartDate = startDate
            startDateEditText?.setText(datePickerHelper?.formatDateForDisplay(startDate))
        }

        if (endDate.isNotEmpty()) {
            selectedEndDate = endDate
            endDateEditText?.setText(datePickerHelper?.formatDateForDisplay(endDate))
        }
    }

    override fun validateForm(formView: View): Boolean {
        var isValid = true

        val titleEditText = formView.findViewById<EditText>(R.id.et_budget_name)
        val tilBudgetName = formView.findViewById<TextInputLayout>(R.id.til_budget_name)
        val balanceEditText = formView.findViewById<EditText>(R.id.et_budget_balance)
        val tilBudgetAmount = formView.findViewById<TextInputLayout>(R.id.til_budget_balance)
        val categoryEditText = formView.findViewById<EditText>(R.id.et_budget_category)
        val tilBudgetCategory = formView.findViewById<TextInputLayout>(R.id.til_budget_category)
        val tilStartDate = formView.findViewById<TextInputLayout>(R.id.til_start_date)
        val tilEndDate = formView.findViewById<TextInputLayout>(R.id.til_end_date)

        // Validate title
        if (!validator.validateRequired(titleEditText, tilBudgetName, "Nama budget tidak boleh kosong")) {
            isValid = false
        }

        // Validate balance
        if (!validator.validateNumeric(balanceEditText, tilBudgetAmount, "Jumlah budget tidak valid")) {
            isValid = false
        }

        // Validate dates
        if (!validator.validateDateSelection(selectedStartDate, tilStartDate, "Pilih tanggal mulai")) {
            isValid = false
        }

        if (!validator.validateDateSelection(selectedEndDate, tilEndDate, "Pilih tanggal selesai")) {
            isValid = false
        }

        // Validate category
        if (!validator.validateRequired(categoryEditText, tilBudgetCategory, "Kategori budget tidak boleh kosong")) {
            isValid = false
        }

        return isValid
    }

    override fun collectFormData(formView: View): BudgetFormData {
        val titleEditText = formView.findViewById<EditText>(R.id.et_budget_name)
        val limitAmountEditText = formView.findViewById<EditText>(R.id.et_budget_balance)
        val categoryEditText = formView.findViewById<EditText>(R.id.et_budget_category)

        return BudgetFormData(
            id = editData?.get("id") as? Int,
            title = titleEditText?.text.toString(),
            limit_amount = limitAmountEditText?.text.toString().toIntOrNull() ?: 0,
            dateStart = selectedStartDate,
            dateEnd = selectedEndDate,
            categoryName = categoryEditText?.text.toString(),
            userId = 1,
            amount = 0
        )
    }

    override fun onDismiss() {
        // Reset budget-specific data
        datePickerHelper = null
        selectedStartDate = ""
        selectedEndDate = ""
    }

    private fun setupBudgetForm(formView: View) {
        // Setup start date picker
        val startDateEditText = formView.findViewById<EditText>(R.id.et_start_date)
        val tilStartDate = formView.findViewById<TextInputLayout>(R.id.til_start_date)

        startDateEditText?.let { editText ->
            editText.setOnClickListener {
                datePickerHelper?.showDatePicker(editText) { apiDate ->
                    selectedStartDate = apiDate
                }
            }
        }

        tilStartDate?.setEndIconOnClickListener {
            datePickerHelper?.showDatePicker(startDateEditText!!) { apiDate ->
                selectedStartDate = apiDate
            }
        }

        // Setup end date picker
        val endDateEditText = formView.findViewById<EditText>(R.id.et_end_date)
        val tilEndDate = formView.findViewById<TextInputLayout>(R.id.til_end_date)

        endDateEditText?.let { editText ->
            editText.setOnClickListener {
                datePickerHelper?.showDatePicker(editText) { apiDate ->
                    selectedEndDate = apiDate
                }
            }
        }

        tilEndDate?.setEndIconOnClickListener {
            datePickerHelper?.showDatePicker(endDateEditText!!) { apiDate ->
                selectedEndDate = apiDate
            }
        }
    }
}