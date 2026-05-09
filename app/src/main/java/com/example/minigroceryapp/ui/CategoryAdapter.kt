package com.example.minigroceryapp.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.minigroceryapp.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categories: List<String>,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String, position: Int) {
            binding.tvCategory.text = category
            
            if (position == selectedPosition) {
                binding.root.setCardBackgroundColor(Color.parseColor("#E91E63"))
                binding.tvCategory.setTextColor(Color.WHITE)
            } else {
                binding.root.setCardBackgroundColor(Color.WHITE)
                binding.tvCategory.setTextColor(Color.parseColor("#333333"))
            }

            binding.root.setOnClickListener {
                val oldPos = selectedPosition
                selectedPosition = position
                notifyItemChanged(oldPos)
                notifyItemChanged(selectedPosition)
                onCategoryClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount() = categories.size
}
