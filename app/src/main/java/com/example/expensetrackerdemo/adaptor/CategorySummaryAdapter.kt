package com.example.expensetrackerdemo.adaptor

import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerdemo.databinding.ItemCategorySummaryBinding
import com.example.expensetrackerdemo.model.CategorySummary

class CategorySummaryAdapter(
    private var items: List<CategorySummary> = listOf()
) : RecyclerView.Adapter<CategorySummaryAdapter.VH>() {

    inner class VH(val binding: ItemCategorySummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategorySummary) {
            binding.tvCategoryName.text = item.category
            binding.tvCategoryAmount.text = "₹${item.totalAmount}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCategorySummaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setData(list: List<CategorySummary>) {
        items = list
        notifyDataSetChanged()
    }
}