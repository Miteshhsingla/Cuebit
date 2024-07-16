package com.timeit.app.DataModels

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime

data class HabitDataModel @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String ?= null,
    val habitName: String ?= null,
    val description: String ?= null,
    val frequency: String ?= null,
    val isCompleted: String ?= "0",
    val reminder: String ?= null,
    val image: Int ?= null,
    val goal: String ?= null,
    val startDate: String ?= null
)

enum class Frequency{
    DAILY,
    WEEKLY,
    INTERVAL
}
