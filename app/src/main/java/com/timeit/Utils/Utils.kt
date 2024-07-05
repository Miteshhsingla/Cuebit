package com.timeit.Utils

import com.timeit.app.DataModels.Day
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

}