package com.example.arto.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arto.R
import com.example.arto.databinding.FragmentHomeBinding
import com.example.arto.ui.home.adapter.TransactionAdapter
import com.example.arto.ui.home.adapter.WalletAdapter
import com.example.arto.utils.FormatCurenrency


class HomeFragment : Fragment() {

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

    private fun setupRecyclerViews() {
        setupWalletRecyclerView()
        setupTransactionRecyclerView()
    }

    private fun setupWalletRecyclerView() {
        walletAdapter = WalletAdapter { walletItem ->
            Toast.makeText(context, "Selected: ${walletItem.name}", Toast.LENGTH_SHORT).show()
        }

        binding.walletRecyclerView.apply {
            adapter = walletAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            clipToPadding = false
            setPadding(16, 0, 16, 0)
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

        // Wallet data
        homeViewModel.wallets.observe(viewLifecycleOwner) { wallets ->
            walletAdapter.submitList(wallets)
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
        // See all wallets
        binding.seeAllWallets.setOnClickListener {
            // Navigate to wallet list
            Toast.makeText(context, "Navigate to Wallet List", Toast.LENGTH_SHORT).show()
        }

        // See all transactions
        binding.seeAllTransactions.setOnClickListener {
            // Navigate to transaction list
            Toast.makeText(context, "Navigate to Transaction List", Toast.LENGTH_SHORT).show()
        }

        // Add transaction button
        binding.addTrx.setOnClickListener {
            // Navigate to add transaction
            Toast.makeText(context, "Add Transaction", Toast.LENGTH_SHORT).show()
        }

        // Notification button
        binding.notificationBtn.setOnClickListener {
            Toast.makeText(context, "Notifications", Toast.LENGTH_SHORT).show()
        }

        // Pull to refresh (if using SwipeRefreshLayout)
        // binding.swipeRefreshLayout?.setOnRefreshListener {
        //     refreshData()
        // }
    }

    private fun showLoading() {
        binding.homeLoading.visibility = View.VISIBLE
        binding.homeParent.alpha = 0.5f

        // Disable interactions
        binding.addTrx.isEnabled = false
        binding.notificationBtn.isEnabled = false
        binding.seeAllWallets.isEnabled = false
        binding.seeAllTransactions.isEnabled = false
    }

    private fun hideLoading() {
        binding.homeLoading.visibility = View.GONE
        binding.homeParent.alpha = 1.0f

        // Enable interactions
        binding.addTrx.isEnabled = true
        binding.notificationBtn.isEnabled = true
        binding.seeAllWallets.isEnabled = true
        binding.seeAllTransactions.isEnabled = true
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun refreshData() {
        homeViewModel.refreshData()
//         binding.swipeRefreshLayout?.isRefreshing = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}