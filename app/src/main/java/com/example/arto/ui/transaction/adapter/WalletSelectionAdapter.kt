package com.example.arto.ui.transaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.arto.R
import com.example.arto.data.model.WalletItem
import com.example.arto.databinding.WalletSelectionItemBinding
import com.example.arto.ui.common.utils.FormatCurenrency

class WalletSelectionAdapter(
    private var wallets: List<WalletItem>,
    private val onWalletSelected: (WalletItem) -> Unit
) : RecyclerView.Adapter<WalletSelectionAdapter.WalletViewHolder>() {

    private var selectedPosition = -1

    fun updateWallets(newWallets: List<WalletItem>) {
        wallets = newWallets
        notifyDataSetChanged()
    }

    fun clearSelection() {
        val oldPosition = selectedPosition
        selectedPosition = -1
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val binding = WalletSelectionItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WalletViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        holder.bind(wallets[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = wallets.size

    inner class WalletViewHolder(
        private val binding: WalletSelectionItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wallet: WalletItem, isSelected: Boolean) {
            with(binding) {
                tvWalletName.text = wallet.name
                tvWalletType.text = wallet.type
                tvWalletBalance.text = FormatCurenrency.format(wallet.balance)

                // Show last 4 digits of account number
                val accountNumber = wallet.rekening.toString()
                tvWalletNumber.text = "••••${accountNumber.takeLast(4)}"

                // Set wallet icon
                ivWalletIcon.setImageResource(getWalletIcon(wallet.name ?: ""))

                // Update selection state
                updateCardAppearance(isSelected)

                root.setOnClickListener {
                    val oldPosition = selectedPosition
                    selectedPosition = adapterPosition

                    if (oldPosition != -1) {
                        notifyItemChanged(oldPosition)
                    }
                    notifyItemChanged(selectedPosition)

                    onWalletSelected(wallet)
                }
            }
        }

        private fun updateCardAppearance(isSelected: Boolean) {
            val context = binding.root.context

            val strokeColor = if (isSelected) {
                ContextCompat.getColor(context, R.color.md_theme_primary)
            } else {
                ContextCompat.getColor(context, R.color.md_theme_outline)
            }

            val strokeWidth = if (isSelected) 3 else 1

            val backgroundColor = if (isSelected) {
                ContextCompat.getColor(context, R.color.md_theme_primaryContainer)
            } else {
                ContextCompat.getColor(context, R.color.md_theme_surface)
            }

            binding.root.strokeColor = strokeColor
            binding.root.strokeWidth = strokeWidth
            binding.root.setCardBackgroundColor(backgroundColor)
        }

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
    }
}