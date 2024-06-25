package com.timeit.app.DataModels

data class Day(
    val dayOfWeek: String,
    val dayNumber: Int,
    val dayMonth: String,
    val year: Int,
    val isToday: Boolean
)