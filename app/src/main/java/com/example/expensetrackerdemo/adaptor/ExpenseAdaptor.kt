package com.example.expensetrackerdemo.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerdemo.databinding.ItemExpenseBinding
import com.example.expensetrackerdemo.model.Expense


class ExpenseAdapter(
    private var items: MutableList<Expense> = mutableListOf(),
    private val onEdit: (Expense) -> Unit,
    private val onDelete: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.VH>() {

    inner class VH(val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(exp: Expense) {
            binding.tvExpenseDescription.text = exp.description
            binding.tvExpenseCategory.text = exp.category

            // --- FIXED DATE HANDLING ---
            // exp.date is already a String (yyyy-MM-dd), so no need to format it again
            binding.tvExpenseDate.text = exp.date

            // If you want pretty formatting like "25 Nov 2025", use this instead:
            /*
            try {
                val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val output = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val parsedDate = input.parse(exp.date)
                binding.tvExpenseDate.text = output.format(parsedDate!!)
            } catch (e: Exception) {
                binding.tvExpenseDate.text = exp.date
            }
            */

            binding.tvExpenseAmount.text = "$${exp.amount}"

            binding.btnEdit.setOnClickListener { onEdit(exp) }
            binding.btnDelete.setOnClickListener { onDelete(exp) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemExpenseBinding.inflate(inflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    fun setData(list: List<Expense>) {
        items = list.toMutableList()
        notifyDataSetChanged()
    }
}