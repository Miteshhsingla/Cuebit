package com.timeit.app.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.timeit.Database.TasksDAO
import com.timeit.app.DataModels.Day
import com.timeit.app.DataModels.TaskDataModel
import com.timeit.app.Fragments.BottomSheetFragment
import com.timeit.app.Fragments.HabitsFragment
import com.timeit.app.Fragments.HomeFragment
import com.timeit.app.Fragments.ProfileFragment
import com.timeit.app.Fragments.TaskFragment
import com.timeit.app.R
import com.timeit.app.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), HomeFragment.OnDateSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        homeFragment.setOnDateSelectedListener(this)

        loadFragment(homeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(homeFragment)
                    true
                }
                R.id.Profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        binding.fab.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }

    override fun onDateSelected(selectedDate: Day, tabPosition: Int) {
        val tasksDAO = TasksDAO(this.applicationContext)
        val calendar = Calendar.getInstance().apply {
            set(selectedDate.year, getMonthIndex(selectedDate.dayMonth), selectedDate.dayNumber)
        }
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        var tasksList: List<TaskDataModel>? = null
        CoroutineScope(Dispatchers.Main).launch {
            tasksList = tasksDAO.getTasksForDate(formattedDate)
        }

        if(tabPosition == 0){
            val taskFragment = TaskFragment.newInstance(selectedDate)
            tasksList?.let { taskFragment.updateTasks(it) }

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, taskFragment)
            transaction.commit()
        } else {
            val habitFragment = HabitsFragment.newInstance()

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, habitFragment)
            transaction.commit()
        }
    }

    private fun getMonthIndex(monthName: String): Int {
        val dateFormatSymbols = DateFormatSymbols.getInstance(Locale.getDefault())
        val monthNames = dateFormatSymbols.months
        return monthNames.indexOf(monthName)
    }
}
