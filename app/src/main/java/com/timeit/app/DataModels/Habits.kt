package com.timeit.app.DataModels

import android.os.Build
import androidx.annotation.RequiresApi
import com.timeit.app.R

object Habits {
    @RequiresApi(Build.VERSION_CODES.O)
    val habits = listOf(
        HabitDataModel(
            habitName = "Morning Jog",
            image = R.drawable.bottom_nav_bg
        ),
        HabitDataModel(
            habitName = "Read a Book",
            image = R.drawable.bottom_nav_bg
        ),
        HabitDataModel(
            habitName = "Drink Water",
            image = R.drawable.bottom_nav_bg
        )
    )
}