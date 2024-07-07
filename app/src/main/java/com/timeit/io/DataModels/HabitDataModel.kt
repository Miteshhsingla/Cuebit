package com.timeit.io.DataModels

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime

data class HabitDataModel @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String ?= null,
    val habitName: String ?= null,
    val category: String ?= null,
    val description: String ?= null,
    val frequency: Frequency ?= null,
    val isCompleted: Boolean ?= false,
    val reminder: LocalTime ?= null,
    val image: Int ?= null,
    val goal: String ?= null,
    val startDate: LocalDate ?= null
)

enum class Frequency{
    DAILY,
    WEEKLY,
    INTERVAL
}
