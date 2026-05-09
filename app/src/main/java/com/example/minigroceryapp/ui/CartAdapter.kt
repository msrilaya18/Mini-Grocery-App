package com.example.minigroceryapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.minigroceryapp.databinding.ItemCartBinding
import com.example.minigroceryapp.model.CartItem

class CartAdapter(
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(DiffCallback) {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.tvName.text = item.name
            binding.tvPrice.text = "₹${item.price * item.quantity}"
            binding.tvQuantity.text = item.quantity.toString()

            binding.btnPlus.setOnClickListener { onIncrease(item) }
            binding.btnMinus.setOnClickListener { onDecrease(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) = oldItem.productId == newItem.productId
            override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) = oldItem == newItem
        }
    }
}
