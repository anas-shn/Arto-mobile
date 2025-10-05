package com.example.arto.ui.common.bottomsheet

import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.example.arto.databinding.BottomSheetFormBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class BaseBottomSheetHelper<T>(protected val fragment: Fragment) {

    protected var bottomSheetDialog: BottomSheetDialog? = null
    protected var binding: BottomSheetFormBinding? = null
    private var onSaveCallback: ((T) -> Unit)? = null
    private var onCancelCallback: (() -> Unit)? = null
    protected var editData: Map<String, Any>? = null
    protected val validator = BottomSheetFormValidator(fragment.requireContext())

    fun setOnSaveListener(callback: (T) -> Unit) {
        this.onSaveCallback = callback
    }

    fun setOnCancelListener(callback: () -> Unit) {
        this.onCancelCallback = callback
    }

    // FIX: Add overloaded method for create mode
    fun showBottomSheet() {
        showBottomSheet(null)
    }

    // FIX: Make editData parameter nullable with default
    fun showBottomSheet(editData: Map<String, Any>? = null) {
        this.editData = editData

        bottomSheetDialog = BottomSheetDialog(fragment.requireContext()).apply {
            binding = BottomSheetFormBinding.inflate(LayoutInflater.from(fragment.requireContext()))
            setContentView(binding!!.root)

            setupBottomSheet()
            setupClickListeners()

            // Pre-fill form if editing
            editData?.let {
                prefillForm(it)
            }

            show()
        }
    }

    protected open fun setupBottomSheet() {
        binding?.apply {
            bottomSheetTitle.text = getTitle()
            inflateFormLayout()
        }
    }

    protected open fun inflateFormLayout() {
        binding?.formContainer?.removeAllViews()
        val formView = LayoutInflater.from(fragment.requireContext())
            .inflate(getLayoutResource(), binding?.formContainer, false)
        binding?.formContainer?.addView(formView)

        setupFormLogic(formView)
    }

    protected open fun setupClickListeners() {
        binding?.apply {
            btnClose.setOnClickListener {
                dismissBottomSheet()
            }

            btnCancel.setOnClickListener {
                onCancelCallback?.invoke()
                dismissBottomSheet()
            }

            btnSave.setOnClickListener {
                val formView = formContainer?.getChildAt(0)
                if (formView != null && validateForm(formView)) {
                    val formData = collectFormData(formView)
                    onSaveCallback?.invoke(formData)
                    dismissBottomSheet()
                }
            }
        }
    }

    protected open fun dismissBottomSheet() {
        bottomSheetDialog?.dismiss()
        bottomSheetDialog = null
        binding = null
        editData = null
        onDismiss()
    }

    // Abstract methods to be implemented by subclasses
    abstract fun getTitle(): String
    abstract fun getLayoutResource(): Int
    abstract fun setupFormLogic(formView: View)
    abstract fun prefillForm(data: Map<String, Any>)
    abstract fun validateForm(formView: View): Boolean
    abstract fun collectFormData(formView: View): T
    abstract fun onDismiss()
}