package com.timeit.Utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.timeit.Database.TasksDAO
import com.timeit.app.Adapters.CategoryAdapter
import com.timeit.app.DataModels.Category
import com.timeit.app.DataModels.Day
import com.timeit.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

public class Utils {

    fun getDayFromDate(date: Calendar): Day {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateString = dateFormat.format(date.time)
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(dateString) ?: Date()

        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""
        val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
        val dayMonth = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
        val year = calendar.get(Calendar.YEAR)

        val isToday = isToday(calendar)

        return Day(dayOfWeek, dayNumber, dayMonth, year, isToday)
    }

    fun generateDaysForMonth(): List<Day> {
        val days = mutableListOf<Day>()
        val calendar = Calendar.getInstance()
        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..daysInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
            val dayMonth = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
            val year = calendar.get(Calendar.YEAR)
            val isToday = i == currentDayOfMonth
            days.add(Day(dayOfWeek ?: "", i, dayMonth, year, isToday))
        }

        return days
    }


    private fun getDateItemList(): List<Day> {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.get(Calendar.DAY_OF_MONTH)
        val weekDays = mutableListOf<Day>()

        calendar.set(Calendar.DAY_OF_MONTH, currentDate)

        for (i in 0..6) {
            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            val dayMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
            val year = calendar.get(Calendar.YEAR)
            val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
            val isToday = i == 0

            weekDays.add(Day(dayOfWeek, dayNumber, dayMonth, year, isToday))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return weekDays
    }

    fun generateDaysForWeek(startingDay: Calendar): List<Day> {
        val days = mutableListOf<Day>()
        val calendar = startingDay.clone() as Calendar

        // Ensure the starting day is Monday
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        for (i in 0 until 7) {
            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""
            val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
            val dayMonth = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
            val year = calendar.get(Calendar.YEAR)
            days.add(Day(
                dayOfWeek = dayOfWeek,
                dayNumber = dayNumber,
                dayMonth = dayMonth,
                year = year,
                isToday = isToday(calendar)
            ))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

    private fun isToday(calendar: Calendar): Boolean {
        val today = Calendar.getInstance()
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }

    @SuppressLint("MissingInflatedId")
    public fun showAddCategoryDialog(context: Context, categoryAdapter: CategoryAdapter) {
        // Create a new instance of the dialog
        val tasksDAO = TasksDAO(context)
        var categoryList: MutableList<Category>
        val dialogView = LayoutInflater.from(context).inflate(R.layout.add_category_dialog, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)

        // Show the dialog
        val alertDialog = dialogBuilder.show()

        // Handle the dialog input
        val editTextCategoryName = dialogView.findViewById<EditText>(R.id.category_name)
        val buttonAddCategory = dialogView.findViewById<TextView>(R.id.SetCategoryButton)
        val crossButton = dialogView.findViewById<ImageView>(R.id.crossButton)

        crossButton.setOnClickListener {
            alertDialog.dismiss()
        }

        buttonAddCategory.setOnClickListener {
            val categoryName = editTextCategoryName.text.toString().trim()
            val categoryId = generateId()

            if (categoryName.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    tasksDAO.addCategory(categoryName, categoryId)
                    categoryList = tasksDAO.fetchAllCategories()
                    if (categoryList.isNotEmpty()) {
                        categoryAdapter.updateAdapter(categoryList)
                    }
                }
                alertDialog.dismiss()
            } else {
                Toast.makeText(context, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateId(): String {
        return UUID.randomUUID().toString()
    }

}