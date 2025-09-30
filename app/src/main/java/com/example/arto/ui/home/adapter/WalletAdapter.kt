package com.example.arto.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.arto.R
import com.example.arto.data.model.WalletItem
import com.example.arto.databinding.WalletLayoutBinding
import com.example.arto.utils.FormatCurenrency


class WalletAdapter(
    private val onItemClick: (WalletItem) -> Unit
) : ListAdapter<WalletItem, WalletAdapter.WalletViewHolder>(WalletDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val binding = WalletLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WalletViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WalletViewHolder(
        private val binding: WalletLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wallet: WalletItem) {
            with(binding) {
                // Set bank name/category
                tvCategoryTitle.text = wallet.type ?: ""

                // Set account number (last 3 digits)
                tvNoRekening.text = "**${wallet.rekening.toString().takeLast(3) ?: "000"}"

                // Format and set amount
                tvWalletAmount.text = FormatCurenrency.format(wallet.balance)

                // Set bank logo/icon
                val iconRes = getBankIcon(wallet.name ?: "")
                ivWalletIcon.setImageResource(iconRes)

                // Click listener
                root.setOnClickListener {
                    onItemClick(wallet)
                }
            }
        }

        private fun getBankIcon(bankCode: String): Int {
            return when (bankCode.lowercase()) {
                "bca" -> R.drawable.logo_bca
                "mandiri" -> R.drawable.mandiri_logo
                "bri" -> R.drawable.bri_logo
                "dana" -> R.drawable.dana_logo
                "gopay" -> R.drawable.gopay_logo
                "shopeepay" -> R.drawable.shopeepay_logo
                else -> R.drawable.ic_wallet_24
            }
        }
    }
}

class WalletDiffCallback : DiffUtil.ItemCallback<WalletItem>() {
    override fun areItemsTheSame(oldItem: WalletItem, newItem: WalletItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WalletItem, newItem: WalletItem): Boolean {
        return oldItem == newItem
    }
}