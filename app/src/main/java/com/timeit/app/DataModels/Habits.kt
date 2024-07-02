package com.timeit.app.DataModels

import android.os.Build
import androidx.annotation.RequiresApi
import com.timeit.app.R

object Habits {
    @RequiresApi(Build.VERSION_CODES.O)
    val habits = listOf(
        HabitDataModel(
            habitName = "Take a Walk",
            image = R.drawable.bottom_nav_bg
        ),
        HabitDataModel(
            habitName = "Walk a Dog",
            image = R.drawable.bottom_nav_bg
        ),
        HabitDataModel(
            habitName = "Drink Water",
            image = R.drawable.bottom_nav_bg
        ),HabitDataModel(
            habitName = "Workout",
            image = R.drawable.bottom_nav_bg
        )
    )
}