package com.timeit.app

import com.timeit.app.DataModels.Day
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

public class Utils {

    fun generateDaysForMonth(): List<Day> {
        val days = mutableListOf<Day>()
        val calendar = Calendar.getInstance()
        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..daysInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
            val isToday = i == currentDayOfMonth
            days.add(Day(dayOfWeek ?: "", i, isToday))
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
            val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
            val isToday = i == 0

            weekDays.add(Day(dayOfWeek, dayNumber, isToday))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return weekDays
    }

    fun generateDaysForWeek(startingDay: Calendar): List<Day> {
        val days = mutableListOf<Day>()
        val calendar = startingDay.clone() as Calendar

        for (i in 0 until 7) {
            days.add(Day(
                dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: "",
                dayNumber = calendar.get(Calendar.DAY_OF_MONTH),
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

}