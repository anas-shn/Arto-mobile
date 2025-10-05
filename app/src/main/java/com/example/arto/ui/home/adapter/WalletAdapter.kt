package com.example.arto.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.arto.R
import com.example.arto.data.model.WalletItem
import com.example.arto.databinding.WalletLayoutBinding
import com.example.arto.databinding.WalletAddButtonLayoutBinding
import com.example.arto.ui.common.utils.FormatCurenrency

class WalletAdapter(
    private val onItemClick: (WalletItem) -> Unit,
    private val onAddWalletClick: () -> Unit
) : ListAdapter<WalletAdapter.WalletDisplayItem, RecyclerView.ViewHolder>(WalletDiffCallback()) {

    companion object {
        private const val TYPE_WALLET = 0
        private const val TYPE_ADD_BUTTON = 1
    }

    // Wrapper class untuk handle wallet dan add button
    sealed class WalletDisplayItem {
        data class WalletData(val wallet: WalletItem) : WalletDisplayItem()
        object AddButton : WalletDisplayItem()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is WalletDisplayItem.WalletData -> TYPE_WALLET
            is WalletDisplayItem.AddButton -> TYPE_ADD_BUTTON
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_WALLET -> {
                val binding = WalletLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                WalletViewHolder(binding)
            }

            TYPE_ADD_BUTTON -> {
                val binding = WalletAddButtonLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AddButtonViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WalletViewHolder -> {
                val item = getItem(position) as WalletDisplayItem.WalletData
                holder.bind(item.wallet)
            }

            is AddButtonViewHolder -> {
                holder.bind()
            }
        }
    }

    // Function untuk update list dengan add button
    fun submitWalletList(wallets: List<WalletItem>) {
        val displayItems = mutableListOf<WalletDisplayItem>()
        displayItems.addAll(wallets.map { WalletDisplayItem.WalletData(it) })
        displayItems.add(WalletDisplayItem.AddButton)
        submitList(displayItems)
    }

    inner class WalletViewHolder(
        private val binding: WalletLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wallet: WalletItem) {
            with(binding) {
                // Set wallet type/category
                tvCategoryTitle.text = wallet.type ?: ""

                // Set account number (last 3 digits)
                val accountNumber = wallet.rekening.toString()
                val maskedNumber = "**${accountNumber.takeLast(3)}"
                tvNoRekening.text = maskedNumber

                // Format and set balance
                tvWalletAmount.text = FormatCurenrency.format(wallet.balance)

                // Set wallet logo/icon - UPDATED
                val iconRes = getWalletIcon(wallet.name ?: "")
                ivWalletIcon.setImageResource(iconRes)

                // Click listener
                root.setOnClickListener {
                    onItemClick(wallet)
                }
            }
        }

        private fun getWalletIcon(walletName: String): Int {
            return when (walletName.lowercase()) {
                // Banks
                "bca" -> R.drawable.bca_logo
                "mandiri" -> R.drawable.mandiri_logo
                "bri" -> R.drawable.bri_logo
                "bni" -> R.drawable.bni_logo
                "cimb niaga", "cimb" -> R.drawable.cimb_logo
                "danamon" -> R.drawable.danamon_logo
                "btn" -> R.drawable.btn_logo

                // E-Wallets
                "dana" -> R.drawable.dana_logo
                "gopay" -> R.drawable.gopay_logo
                "shopeepay", "shopee pay" -> R.drawable.shopeepay_logo
                "ovo" -> R.drawable.ovo_logo
                "linkaja", "link aja" -> R.drawable.linkaja_logo
                "paypal" -> R.drawable.paypal_logo
                "jenius" -> R.drawable.jenius_logo

                else -> R.drawable.ic_wallet_24
            }
        }
    }

    inner class AddButtonViewHolder(
        private val binding: WalletAddButtonLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.btnAddWallet.setOnClickListener {
                onAddWalletClick()
            }

            // No margin untuk item terakhir
            val layoutParams = binding.root.layoutParams as? RecyclerView.LayoutParams
            layoutParams?.marginEnd = 0
            binding.root.layoutParams = layoutParams
        }
    }

    class WalletDiffCallback : DiffUtil.ItemCallback<WalletDisplayItem>() {
        override fun areItemsTheSame(
            oldItem: WalletDisplayItem,
            newItem: WalletDisplayItem
        ): Boolean {
            return when {
                oldItem is WalletDisplayItem.WalletData && newItem is WalletDisplayItem.WalletData -> {
                    oldItem.wallet.id == newItem.wallet.id
                }

                oldItem is WalletDisplayItem.AddButton && newItem is WalletDisplayItem.AddButton -> {
                    true
                }

                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: WalletDisplayItem,
            newItem: WalletDisplayItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}