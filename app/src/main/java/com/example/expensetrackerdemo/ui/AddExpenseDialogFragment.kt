package com.example.expensetrackerdemo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.expensetrackerdemo.R
import com.example.expensetrackerdemo.databinding.FragmentAddExpenseDialogBinding
import com.example.expensetrackerdemo.model.Expense
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*
import kotlin.let
import kotlin.text.isEmpty
import kotlin.text.toDoubleOrNull
import kotlin.text.trim
import kotlin.toString

class AddExpenseDialogFragment : BottomSheetDialogFragment() {

    private var expenseToEdit: Expense? = null
    private var onSaveCallback: ((Expense) -> Unit)? = null
    private lateinit var binding: FragmentAddExpenseDialogBinding

    companion object {
        fun newInstance(
            expense: Expense?,
            onSave: (Expense) -> Unit
        ): AddExpenseDialogFragment {
            val fragment = AddExpenseDialogFragment()
            fragment.expenseToEdit = expense
            fragment.onSaveCallback = onSave
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddExpenseDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryDropdown()
        setupDatePicker()
        setupInitialValues()
        setupButtons()
    }

    private fun setupCategoryDropdown() {
        val categories = listOf("Food", "Travel", "Shopping", "Bills", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        binding.actvCategoryDialog.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build()

            datePicker.addOnPositiveButtonClickListener { timestamp ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = sdf.format(Date(timestamp))
                binding.etDate.setText(formattedDate)
            }

            datePicker.show(parentFragmentManager, "date_picker")
        }
    }

    private fun setupInitialValues() {
        expenseToEdit?.let { exp ->
            binding.tvDialogTitle.text = "Edit Expense"
            binding.etAmount.setText(exp.amount.toString())
            binding.etDescription.setText(exp.description)
            binding.actvCategoryDialog.setText(exp.category, false)
            binding.etDate.setText(exp.date)
        }
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {

            val amountStr = binding.etAmount.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()
            val category = binding.actvCategoryDialog.text.toString().trim()
            val date = binding.etDate.text.toString().trim()

            // VALIDATION
            if (amountStr.isEmpty()) {
                binding.tilAmount.error = "Amount required"
                return@setOnClickListener
            } else binding.tilAmount.error = null

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                binding.tilAmount.error = "Invalid amount"
                return@setOnClickListener
            }

            if (desc.isEmpty()) {
                binding.tilDescription.error = "Description required"
                return@setOnClickListener
            } else binding.tilDescription.error = null

            if (category.isEmpty()) {
                binding.tilCategory.error = "Select category"
                return@setOnClickListener
            } else binding.tilCategory.error = null

            if (date.isEmpty()) {
                binding.tilDate.error = "Select date"
                return@setOnClickListener
            } else binding.tilDate.error = null

            // CREATE EXPENSE OBJECT
            val expense = Expense(
                id = expenseToEdit?.id,
                amount = amount,
                description = desc,
                category = category,
                date = date
            )

            onSaveCallback?.invoke(expense)
            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()

            dismiss()
        }
    }
}
