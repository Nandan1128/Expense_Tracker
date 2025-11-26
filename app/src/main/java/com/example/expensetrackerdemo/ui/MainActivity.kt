package com.example.expensetrackerdemo.ui

import android.R
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetrackerdemo.adaptor.CategorySummaryAdapter
import com.example.expensetrackerdemo.adaptor.ExpenseAdapter
import com.example.expensetrackerdemo.databinding.ActivityMainBinding
import com.example.expensetrackerdemo.model.CategorySummary
import com.example.expensetrackerdemo.model.Expense
import com.example.expensetrackerdemo.viewmodel.ExpenseViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.forEach
import kotlin.collections.groupBy
import kotlin.collections.map
import kotlin.collections.sumOf
import kotlin.collections.toList
import kotlin.let
import kotlin.takeIf
import kotlin.text.format
import kotlin.text.ifBlank
import kotlin.text.isNullOrEmpty
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: ExpenseViewModel by viewModels()

    private lateinit var categorySummaryAdapter: CategorySummaryAdapter

    private lateinit var expenseAdapter: ExpenseAdapter
   // private lateinit var summaryAdapter: CategorySummaryAdapter // if needed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Filter Date Picker
        binding.btnStartDate.setOnClickListener { showDatePicker { date ->
            binding.btnStartDate.text = date
        }}

        binding.btnEndDate.setOnClickListener { showDatePicker { date ->
            binding.btnEndDate.text = date
        }}

        // Filter CATEGORY DROPDOWN
        val categories = listOf("Food", "Travel", "Shopping", "Bills", "Other")
        val dropdownAdapter = ArrayAdapter(this, R.layout.simple_list_item_1, categories)
        binding.actvCategory.setAdapter(dropdownAdapter)

        categorySummaryAdapter = CategorySummaryAdapter()
        binding.rvCategorySummary.adapter = categorySummaryAdapter
        binding.rvCategorySummary.layoutManager = LinearLayoutManager(this)


        // EXPENSE LIST
        expenseAdapter = ExpenseAdapter(
            onEdit = { expense -> openEditDialog(expense) },
            onDelete = { expense -> expense.id?.let { vm.deleteExpense(it) } }
        )

        binding.rvExpenses.layoutManager = LinearLayoutManager(this)
        binding.rvExpenses.adapter = expenseAdapter

        // CATEGORY SUMMARY LIST
        binding.rvCategorySummary.layoutManager = LinearLayoutManager(this)
        // Add adapter if implemented

        // OBSERVERS
        vm.expenses.observe(this) { list ->
            expenseAdapter.setData(list)
            updateDashboard(list)
        }

        vm.loading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        vm.error.observe(this) {
            if (!it.isNullOrEmpty()) Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        // ADD EXPENSE BUTTON
        binding.fabAddExpense.setOnClickListener {
            openAddDialog()
        }

        // FILTER BUTTON
        binding.btnApplyFilter.setOnClickListener {
            val category = binding.actvCategory.text.toString().ifBlank { null }
            val start = binding.btnStartDate.text.toString().takeIf { it != "Start Date" }
            val end = binding.btnEndDate.text.toString().takeIf { it != "End Date" }
            vm.fetchExpenses(category=category, from=start, to=end)
        }

        vm.fetchExpenses()
    }


    private fun showDatePicker(onSelected: (String) -> Unit) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .build()

        datePicker.addOnPositiveButtonClickListener { timestamp ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatted = sdf.format(Date(timestamp))
            onSelected(formatted)
        }

        datePicker.show(supportFragmentManager, "date_picker")
    }


//    private fun updateDashboard(list: List<Expense>) {
//
//        // 1. TOTAL SPENDING
//        val total = list.sumOf { it.amount }
//        binding.tvTotalSpending.text = "Total: $${"%.2f".format(total)}"
//
//        // 2. CATEGORY SUMMARY
//        val summary = list
//            .groupBy { it.category }
//            .map { (category, expenses) ->
//                CategorySummary(
//                    category = category,
//                    totalAmount = expenses.sumOf { it.amount }
//                )
//            }
//
//        categorySummaryAdapter.setData(summary)
//    }


    private fun openAddDialog() {
        val dialog = AddExpenseDialogFragment.Companion.newInstance(null) { exp ->
            vm.addExpense(exp) { success ->
                if (success) Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show(supportFragmentManager, "add")
    }

    private fun openEditDialog(expense: Expense) {
        val dialog = AddExpenseDialogFragment.Companion.newInstance(expense) { newExp ->
            expense.id?.let { vm.updateExpense(it, newExp) {} }
        }
        dialog.show(supportFragmentManager, "edit")
    }
    private fun updateDashboard(list: List<Expense>) {

        // 1. Total spending
        val total = list.sumOf { it.amount }
        binding.tvTotalSpending.text = "Total: $${"%.2f".format(total)}"

        // 2. Category summary list
        val summary = list
            .groupBy { it.category }
            .map { (category, expenses) ->
                CategorySummary(
                    category = category,
                    totalAmount = expenses.sumOf { it.amount }
                )
            }

        categorySummaryAdapter.setData(summary)

        // 3. PIE CHART DATA
        val entries = kotlin.collections.ArrayList<PieEntry>()

        summary.forEach {
            entries.add(PieEntry(it.totalAmount.toFloat(), it.category))
        }

        val dataSet = PieDataSet(entries, "Expense Breakdown")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val data = PieData(dataSet)
        data.setValueTextSize(12f)

        binding.pieChart.data = data
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.animateY(1200)
        binding.pieChart.invalidate() // refresh
    }

}