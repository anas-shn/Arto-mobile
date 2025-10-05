package com.example.arto.ui.home.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arto.R
import com.example.arto.data.model.WalletItem
import com.example.arto.databinding.FragmentHomeBinding
import com.example.arto.databinding.WalletModalBinding
import com.example.arto.ui.home.viewmodel.HomeViewModel
import com.example.arto.ui.home.adapter.WalletAdapter
import com.example.arto.ui.common.utils.FormatCurenrency
import com.example.arto.ui.home.adapter.TransactionAdapter
import com.example.arto.ui.wallet.helper.WalletBottomSheetHelper
import com.example.arto.ui.wallet.helper.WalletFormData
import com.google.android.material.bottomsheet.BottomSheetDialog

class HomeFragment : Fragment() {

    private lateinit var walletBottomSheetHelper: WalletBottomSheetHelper
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var walletAdapter: WalletAdapter
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Initialize BottomSheet Helper
        setupBottomSheetHelper()

        // Setup UI components
        setupRecyclerViews()
        setupObservers()
        setupClickListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load initial data
        homeViewModel.fetchWallets()
        homeViewModel.fetchTransactions()
    }

    private fun setupBottomSheetHelper() {
        walletBottomSheetHelper = WalletBottomSheetHelper(this)

        // Setup listeners
        setupBottomSheetListeners()
    }

    private fun setupBottomSheetListeners() {
        // Wallet bottom sheet listener
        walletBottomSheetHelper.setOnSaveListener { data ->
            handleWalletFormSubmit(data)
        }

        walletBottomSheetHelper.setOnCancelListener {
            // Handle cancel if needed
        }
    }

    private fun setupRecyclerViews() {
        setupWalletRecyclerView()
        setupTransactionRecyclerView()
    }

    private fun setupWalletRecyclerView() {
        walletAdapter = WalletAdapter(
            onItemClick = { walletItem ->
                showWalletModal(walletItem) // Show modal instead of toast
            },
            onAddWalletClick = {
                // FIX: Call without parameter for create mode
                walletBottomSheetHelper.showBottomSheet()
            }
        )

        binding.walletRecyclerView.apply {
            adapter = walletAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            setPadding(8, 0, 8, 0)
            clipToPadding = false
        }
    }

    // Add wallet modal function
    private fun showWalletModal(wallet: WalletItem) {
        val modalBinding = WalletModalBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(modalBinding.root)

        // Populate modal data
        with(modalBinding) {
            tvPopupWalletName.text = wallet.name
            tvPopupWalletType.text = wallet.type
            tvPopupWalletBalance.text = FormatCurenrency.format(wallet.balance)
            tvPopupWalletNumber.text = "**${wallet.rekening.toString().takeLast(4)}"

            // Set wallet icon
            val iconRes = getWalletIcon(wallet.name)
            ivPopupWalletIcon.setImageResource(iconRes)

            // Edit button click
            btnEditWallet.setOnClickListener {
                bottomSheetDialog.dismiss()
                editWallet(wallet)
            }

            // Delete button click
            btnDeleteWallet.setOnClickListener {
                bottomSheetDialog.dismiss()
                showDeleteConfirmation(wallet)
            }
        }

        bottomSheetDialog.show()
    }

    // FIX: Edit wallet function
    private fun editWallet(wallet: WalletItem) {
        val editData = mapOf(
            "id" to wallet.id,
            "name" to wallet.name,
            "balance" to wallet.balance,
            "type" to wallet.type,
            "rekening" to wallet.rekening,
            "user_id" to wallet.user_id
        )
        // FIX: Pass editData to showBottomSheet for edit mode
        walletBottomSheetHelper.showBottomSheet(editData)
    }

    // Add delete confirmation
    private fun showDeleteConfirmation(wallet: WalletItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Wallet")
            .setMessage("Apakah Anda yakin ingin menghapus wallet ${wallet.name}?")
            .setPositiveButton("Hapus") { _, _ ->
                homeViewModel.deleteWallet(wallet.id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // Add helper function for wallet icon
    private fun getWalletIcon(walletName: String): Int {
        return when (walletName.lowercase()) {
            "bca" -> R.drawable.bca_logo
            "mandiri" -> R.drawable.mandiri_logo
            "bri" -> R.drawable.bri_logo
            "bni" -> R.drawable.bni_logo
            "cimb niaga", "cimb" -> R.drawable.cimb_logo
            "danamon" -> R.drawable.danamon_logo
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

    private fun handleWalletFormSubmit(data: WalletFormData) {
        if (data.id != null) {
            // Update wallet
            homeViewModel.updateWallet(
                id = data.id,
                name = data.name,
                balance = data.balance,
                type = data.type,
                rekening = data.rekening
            )
            Toast.makeText(context, "Wallet berhasil diupdate", Toast.LENGTH_SHORT).show()
        } else {
            // Create wallet
            homeViewModel.createWallet(
                name = data.name,
                balance = data.balance,
                type = data.type,
                rekening = data.rekening
            )
            Toast.makeText(context, "Wallet berhasil dibuat", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupTransactionRecyclerView() {
        transactionAdapter = TransactionAdapter { transactionItem ->
            Toast.makeText(context, "Selected: ${transactionItem.title}", Toast.LENGTH_SHORT).show()
        }

        binding.transactionRecyclerView.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupObservers() {
        // Loading state
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        // Observe user name
        homeViewModel.name.observe(viewLifecycleOwner) { name ->
            binding.nameUser.text = name
        }

        // Welcome text
        homeViewModel.text.observe(viewLifecycleOwner) { text ->
            binding.welcomeText.text = text
        }

        // Total Balance
        homeViewModel.totalBalance.observe(viewLifecycleOwner) { totalBalance ->
            binding.totalValue.text = FormatCurenrency.format(totalBalance)
        }

        // Total Income
        homeViewModel.totalIncome.observe(viewLifecycleOwner) { totalIncome ->
            binding.incomeValue.text = FormatCurenrency.format(totalIncome)
        }

        // Total Outcome
        homeViewModel.totalOutcome.observe(viewLifecycleOwner) { totalOutcome ->
            binding.outcomeValue.text = FormatCurenrency.format(totalOutcome)
        }

        // Wallet data - menggunakan submitWalletList untuk include add button
        homeViewModel.wallets.observe(viewLifecycleOwner) { wallets ->
            walletAdapter.submitWalletList(wallets)
        }

        // Transaction data
        homeViewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
        }

        // Error handling
        homeViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
            }
        }
    }

    private fun setupClickListeners() {
        // See all wallets - Navigate to wallet list

        // See all transactions - Navigate to transaction list
        binding.seeAllTransactions.setOnClickListener {
            // Navigate to summary with proper pop behavior
            findNavController().navigate(
                R.id.navigation_summary,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .build()
            )
       }


        // Notification button
        binding.notificationBtn.setOnClickListener {
            Toast.makeText(context, "Notifications", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading() {
        binding.homeLoading.visibility = View.VISIBLE
        binding.homeParent.alpha = 0.5f

        // Disable interactions
//        binding.addTrx.isEnabled = false
        binding.notificationBtn.isEnabled = false
//        binding.seeAllWallets.isEnabled = false
        binding.seeAllTransactions.isEnabled = false
    }

    private fun hideLoading() {
        binding.homeLoading.visibility = View.GONE
        binding.homeParent.alpha = 1.0f

        // Enable interactions
//        binding.addTrx.isEnabled = true
        binding.notificationBtn.isEnabled = true
//        binding.seeAllWallets.isEnabled = true
        binding.seeAllTransactions.isEnabled = true
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun refreshData() {
        homeViewModel.refreshData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}