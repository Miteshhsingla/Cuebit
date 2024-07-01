package com.timeit.app.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.timeit.app.DataModels.Day
import com.timeit.app.Fragments.BottomSheetFragment
import com.timeit.app.Fragments.HomeFragment
import com.timeit.app.Fragments.ProfileFragment
import com.timeit.app.Fragments.TaskFragment
import com.timeit.app.R
import com.timeit.app.databinding.ActivityMainBinding

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

    override fun onDateSelected(selectedDate: Day) {
        Log.d(TAG, "onDateSelected: $selectedDate")
        val taskFragment = TaskFragment.newInstance(selectedDate)
        taskFragment.updateTasksForDate(selectedDate, this@MainActivity.applicationContext)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
