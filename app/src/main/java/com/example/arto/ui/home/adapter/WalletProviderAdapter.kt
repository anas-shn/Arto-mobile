package com.example.arto.ui.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.arto.R
import com.example.arto.databinding.WalletCardItemBinding

data class WalletProvider(
    val name: String,
    val type: String,
    val logoRes: Int
)

class WalletProviderAdapter(
    private val onProviderSelected: (WalletProvider) -> Unit
) : ListAdapter<WalletProvider, WalletProviderAdapter.ProviderViewHolder>(ProviderDiffCallback()) {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder {
        val binding = WalletCardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProviderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    override fun submitList(list: List<WalletProvider>?) {
        Log.d("WalletProviderAdapter", "submitList called with ${list?.size} items")
        list?.forEach { provider ->
            Log.d("WalletProviderAdapter", "Provider: ${provider.name}, Type: ${provider.type}")
        }
        super.submitList(list)
    }

    fun setSelectedProvider(provider: WalletProvider) {
        val newPosition = currentList.indexOfFirst { 
            it.name == provider.name && it.type == provider.type 
        }
        val oldPosition = selectedPosition
        selectedPosition = newPosition

        if (oldPosition != -1) notifyItemChanged(oldPosition)
        if (newPosition != -1) notifyItemChanged(newPosition)
        
        // Call the callback to update selected provider
        if (newPosition != -1) {
            onProviderSelected(provider)
        }
    }

    fun clearSelection() {
        val oldPosition = selectedPosition
        selectedPosition = -1
        if (oldPosition != -1) notifyItemChanged(oldPosition)
    }

    inner class ProviderViewHolder(
        private val binding: WalletCardItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(provider: WalletProvider, isSelected: Boolean) {
            Log.d("WalletProviderAdapter", "Binding provider: ${provider.name}")
            
            with(binding) {
                // Set text and image
                tvProviderName.text = provider.name
                ivProviderLogo.setImageResource(provider.logoRes)

                // Update selection state
                val strokeColor = if (isSelected) {
                    ContextCompat.getColor(root.context, R.color.md_theme_primary)
                } else {
                    ContextCompat.getColor(root.context, R.color.md_theme_outline)
                }

                val strokeWidth = if (isSelected) 3 else 1

                root.strokeColor = strokeColor
                root.strokeWidth = strokeWidth

                // Update background color for selection
                val backgroundColor = if (isSelected) {
                    ContextCompat.getColor(root.context, R.color.md_theme_primaryContainer)
                } else {
                    ContextCompat.getColor(root.context, R.color.md_theme_surface)
                }
                root.setCardBackgroundColor(backgroundColor)

                root.setOnClickListener {
                    val oldPosition = selectedPosition
                    selectedPosition = adapterPosition

                    if (oldPosition != -1 && oldPosition != selectedPosition) {
                        notifyItemChanged(oldPosition)
                    }
                    notifyItemChanged(selectedPosition)

                    onProviderSelected(provider)
                }
            }
        }
    }

    class ProviderDiffCallback : DiffUtil.ItemCallback<WalletProvider>() {
        override fun areItemsTheSame(oldItem: WalletProvider, newItem: WalletProvider): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: WalletProvider, newItem: WalletProvider): Boolean {
            return oldItem == newItem
        }
    }
}