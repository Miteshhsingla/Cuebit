package com.timeit.app

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.timeit.app.DataModels.Day
import com.timeit.app.DataModels.TaskDataModel
import com.timeit.app.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val tasks = listOf(
        TaskDataModel("Meeting", "Team meeting at 10 AM", 24),
        TaskDataModel("Lunch", "Lunch with client", 24),
        TaskDataModel("Workout", "Evening workout", 25),
        TaskDataModel("Presentation", "Project presentation", 26),
        TaskDataModel("Dinner", "Dinner with family", 27)
    )

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
        setCurrentMonth()
        // Call function to update week view
        updateWeekView()

    }

    private fun setCurrentMonth() {
        val calendar = Calendar.getInstance()
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthYearText = monthYearFormat.format(calendar.time)
        val monthYearTextView = findViewById<TextView>(R.id.selectedDayText)
        monthYearTextView.text = monthYearText
    }

    private fun updateWeekView() {
        // Get current date and setup week data
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val currentDate = calendar.get(Calendar.DAY_OF_MONTH)

        // List to hold days of the week
        val weekDays = mutableListOf<Day>()

        // Initialize the calendar to start from today
        calendar.set(Calendar.DAY_OF_MONTH, currentDate)

        // Set up days of the week starting from today
        for (i in 0..6) {
            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
            val isToday = i == 0  // today is the first day in the list

            weekDays.add(Day(dayOfWeek, dayNumber, isToday))

            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Populate views with days of the week
        weekDays.forEachIndexed { index, day ->
            val blockId = resources.getIdentifier("block${index + 1}", "id", packageName)
            val block = findViewById<ConstraintLayout>(blockId)

            val dayOfWeekId = resources.getIdentifier("weekdayText${index + 1}", "id", packageName)
            val dayNumberId =
                resources.getIdentifier("weekdayNumber${index + 1}", "id", packageName)
            val todayDotId = resources.getIdentifier("todayDot${index + 1}", "id", packageName)

            val dayOfWeekText = findViewById<TextView>(dayOfWeekId)
            val dayNumberText = findViewById<TextView>(dayNumberId)
            val todayDot = findViewById<ImageView>(todayDotId)

            dayOfWeekText.text = day.dayOfWeek
            dayNumberText.text = day.dayNumber.toString()

            showTasksForDay(day.dayNumber)
            if (day.isToday) {
                block.setBackgroundResource(R.drawable.selected_day_bg)
                dayOfWeekText.setTextColor(Color.WHITE)  // assuming WeekDayTopTextSelected has white text
                dayNumberText.setTextColor(Color.WHITE)  // assuming WeekDayTopNumberSelected has white text
                todayDot.isVisible = true
            } else {
                block.setBackgroundColor(Color.TRANSPARENT)
                dayOfWeekText.setTextColor(Color.BLACK)  // assuming WeekDayTopText has black text
                dayNumberText.setTextColor(Color.BLACK)  // assuming WeekDayTopNumber has black text
                todayDot.isVisible = false
            }

            block.setOnClickListener {
                showTasksForDay(day.dayNumber)
                block.setBackgroundResource(R.drawable.selected_day_bg)
                dayOfWeekText.setTextColor(Color.WHITE)  // assuming WeekDayTopTextSelected has white text
                dayNumberText.setTextColor(Color.WHITE)
            }
        }

    }

    private fun showTasksForDay(dayOfMonth: Int) {
        val tasksContainer = findViewById<LinearLayout>(R.id.tasksContainer)
        tasksContainer.removeAllViews()

        val inflater = LayoutInflater.from(this)
        val tasksForDay = tasks.filter { it.dayOfMonth == dayOfMonth }

        tasksForDay.forEach { task ->
            val taskView = inflater.inflate(R.layout.task_item, tasksContainer, false)
            val taskTitle = taskView.findViewById<TextView>(R.id.taskTitle)
            val taskDescription = taskView.findViewById<TextView>(R.id.taskDescription)

            taskTitle.text = task.title
            taskDescription.text = task.description

            tasksContainer.addView(taskView)
        }
    }
}
