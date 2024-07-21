package com.cuebit.io.DataModels

import android.os.Build
import androidx.annotation.RequiresApi
import com.cuebit.io.R

object Habits {
    @RequiresApi(Build.VERSION_CODES.O)
    val habits = listOf(
        HabitDataModel(
            habitName = "Take a Walk",
            image = R.drawable.delete_pop_icon
        ),
        HabitDataModel(
            habitName = "Walk a Dog",
            image = R.drawable.delete_pop_icon
        ),
        HabitDataModel(
            habitName = "Drink Water",
            image = R.drawable.delete_pop_icon
        ),HabitDataModel(
            habitName = "Workout",
            image = R.drawable.delete_pop_icon
        )
    )
}