package com.example.arto.ui.wallet.helper

import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arto.R
import com.example.arto.ui.home.adapter.WalletProvider
import com.example.arto.ui.home.adapter.WalletProviderAdapter
import com.example.arto.ui.common.bottomsheet.BaseBottomSheetHelper
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout

data class WalletFormData(
    val id: Int? = null,
    val name: String,
    val balance: Int,
    val userId: Int,
    val type: String,
    val rekening: Long
)

class WalletBottomSheetHelper(fragment: Fragment) : BaseBottomSheetHelper<WalletFormData>(fragment) {

    private var selectedProvider: WalletProvider? = null
    private var selectedWalletType: String = "Bank"

    override fun getTitle(): String {
        return if (editData != null) "Edit Wallet" else "Tambah Wallet"
    }

    override fun getLayoutResource(): Int = R.layout.form_wallet

    override fun setupFormLogic(formView: View) {
        setupWalletForm(formView)
    }

    override fun prefillForm(data: Map<String, Any>) {
        val formView = binding?.formContainer?.getChildAt(0) ?: return

        val balanceEditText = formView.findViewById<EditText>(R.id.et_wallet_balance)
        val numberEditText = formView.findViewById<EditText>(R.id.et_wallet_number)

        balanceEditText?.setText((data["balance"] as? Int)?.toString() ?: "")
        numberEditText?.setText((data["rekening"] as? Long)?.toString() ?: "")

        // Set provider dengan proper pre-selection
        val walletName = data["name"] as? String
        val walletType = data["type"] as? String

        if (!walletName.isNullOrEmpty() && !walletType.isNullOrEmpty()) {
            selectedProvider = WalletProvider(walletName, walletType, getProviderLogo(walletName))
            selectedWalletType = walletType

            // Pre-select provider in adapter setelah form di-setup
            formView.post {
                preselectProviderInAdapter(walletName, walletType)
            }
        }
    }

    override fun validateForm(formView: View): Boolean {
        var isValid = true

        val balanceEditText = formView.findViewById<EditText>(R.id.et_wallet_balance)
        val tilWalletBalance = formView.findViewById<TextInputLayout>(R.id.til_wallet_balance)
        val numberEditText = formView.findViewById<EditText>(R.id.et_wallet_number)
        val tilWalletNumber = formView.findViewById<TextInputLayout>(R.id.til_wallet_number)

        // Validate balance
        if (!validator.validateNumeric(balanceEditText, tilWalletBalance, "Saldo tidak valid", true)) {
            isValid = false
        }

        // Validate account number
        if (!validator.validateLong(numberEditText, tilWalletNumber, "Nomor rekening tidak valid")) {
            isValid = false
        }

        // Validate provider selection
        if (selectedProvider == null) {
            isValid = false
        }

        return isValid
    }

    override fun collectFormData(formView: View): WalletFormData {
        val balanceEditText = formView.findViewById<EditText>(R.id.et_wallet_balance)
        val numberEditText = formView.findViewById<EditText>(R.id.et_wallet_number)

        return WalletFormData(
            id = editData?.get("id") as? Int,
            name = selectedProvider?.name ?: "",
            balance = balanceEditText?.text.toString().toIntOrNull() ?: 0,
            userId = 1,
            type = selectedProvider?.type ?: "Bank",
            rekening = numberEditText?.text.toString().toLongOrNull() ?: 0L
        )
    }

    override fun onDismiss() {
        // Reset wallet-specific data
        selectedProvider = null
        selectedWalletType = "Bank"
    }

    private fun setupWalletForm(formView: View) {
        val tabLayout = formView.findViewById<TabLayout>(R.id.tab_wallet_type)
        val recyclerView = formView.findViewById<RecyclerView>(R.id.rv_wallet_providers)

        selectedWalletType = "Bank"
        selectedProvider = null

        // Create adapter
        val providerAdapter = WalletProviderAdapter { provider ->
            selectedProvider = provider
            selectedWalletType = provider.type
        }

        // Setup RecyclerView
        recyclerView?.let { rv ->
            rv.layoutManager = GridLayoutManager(fragment.requireContext(), 4)
            rv.adapter = providerAdapter

            // Load initial data based on edit mode
            val currentEditData = this.editData
            if (currentEditData != null) {
                val walletType = currentEditData["type"] as? String ?: "Bank"
                selectedWalletType = walletType

                // Set correct tab
                val tabIndex = if (walletType == "Ewallet") 1 else 0
                tabLayout?.getTabAt(tabIndex)?.select()

                // Load providers for that type
                if (walletType == "Ewallet") {
                    providerAdapter.submitList(getEWalletProviders())
                } else {
                    providerAdapter.submitList(getBankProviders())
                }
            } else {
                // Load initial bank providers for new wallet
                providerAdapter.submitList(getBankProviders())
            }
        }

        // Setup tabs
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        selectedWalletType = "Bank"
                        providerAdapter.submitList(getBankProviders())
                    }
                    1 -> {
                        selectedWalletType = "Ewallet"
                        providerAdapter.submitList(getEWalletProviders())
                    }
                }

                // Only clear selection if not in edit mode
                if (editData == null) {
                    providerAdapter.clearSelection()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun preselectProviderInAdapter(walletName: String, walletType: String) {
        val recyclerView = binding?.formContainer?.getChildAt(0)?.findViewById<RecyclerView>(R.id.rv_wallet_providers)
        val adapter = recyclerView?.adapter as? WalletProviderAdapter

        adapter?.let { providerAdapter ->
            val targetProvider = WalletProvider(walletName, walletType, getProviderLogo(walletName))
            providerAdapter.setSelectedProvider(targetProvider)
        }
    }

    private fun getBankProviders(): List<WalletProvider> {
        return listOf(
            WalletProvider("BCA", "Bank", R.drawable.bca_logo),
            WalletProvider("Mandiri", "Bank", R.drawable.mandiri_logo),
            WalletProvider("BRI", "Bank", R.drawable.bri_logo),
            WalletProvider("BNI", "Bank", R.drawable.bni_logo),
            WalletProvider("CIMB", "Bank", R.drawable.cimb_logo),
            WalletProvider("Danamon", "Bank", R.drawable.danamon_logo),
            WalletProvider("BSI", "Bank", R.drawable.bsi_logo),
            WalletProvider("BTN", "Bank", R.drawable.btn_logo),
        )
    }

    private fun getEWalletProviders(): List<WalletProvider> {
        return listOf(
            WalletProvider("DANA", "Ewallet", R.drawable.dana_logo),
            WalletProvider("GoPay", "Ewallet", R.drawable.gopay_logo),
            WalletProvider("ShopeePay", "Ewallet", R.drawable.shopeepay_logo),
            WalletProvider("OVO", "Ewallet", R.drawable.ovo_logo),
            WalletProvider("LinkAja", "Ewallet", R.drawable.linkaja_logo),
        )
    }

    private fun getProviderLogo(walletName: String): Int {
        return when (walletName.lowercase()) {
            "bca" -> R.drawable.bca_logo
            "mandiri" -> R.drawable.mandiri_logo
            "bri" -> R.drawable.bri_logo
            "bni" -> R.drawable.bni_logo
            "cimb niaga", "cimb" -> R.drawable.cimb_logo
            "danamon" -> R.drawable.danamon_logo
            "bsi" -> R.drawable.bsi_logo
            "btn" -> R.drawable.btn_logo
            "dana" -> R.drawable.dana_logo
            "gopay" -> R.drawable.gopay_logo
            "shopeepay" -> R.drawable.shopeepay_logo
            "ovo" -> R.drawable.ovo_logo
            "linkaja" -> R.drawable.linkaja_logo
            "jenius" -> R.drawable.jenius_logo
            else -> R.drawable.ic_wallet_24
        }
    }
}