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
            
            val context = binding.root.context
            val typedValue = android.util.TypedValue()
            
            if (position == selectedPosition) {
                binding.root.setCardBackgroundColor(Color.parseColor("#E91E63"))
                binding.tvCategory.setTextColor(Color.WHITE)
            } else {
                context.theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true)
                binding.root.setCardBackgroundColor(typedValue.data)
                context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
                binding.tvCategory.setTextColor(typedValue.data)
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
