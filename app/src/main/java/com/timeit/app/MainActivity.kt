package com.timeit.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.timeit.app.Fragments.HabitsFragment
import com.timeit.app.Fragments.HomeFragment
import com.timeit.app.Fragments.ProfileFragment
import com.timeit.app.Fragments.TaskFragment
import com.timeit.app.databinding.ActivityMainBinding
import android.util.Log
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.Profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        val bottomSheetView = layoutInflater.inflate(R.layout.activity_bottom_sheet, null)
        dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        dialog.setCanceledOnTouchOutside(true)

        binding.fab.setOnClickListener {
            dialog.show()
        }

        val tabMode: TabLayout = bottomSheetView.findViewById(R.id.tabModeFAB)
        tabMode.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val fragment = when (tab?.position) {
                    0 -> TaskFragment.newInstance()
                    1 -> HabitsFragment.newInstance()
                    else -> null
                }

                fragment?.let {
                    val containerView = bottomSheetView.findViewById<FrameLayout>(R.id.fragmentContainerBottomSheet)
                    if (containerView != null) {
                        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragmentContainerBottomSheet,it )
                        transaction.commit()
                    } else {
                        Log.e("MainActivity", "Fragment container not found")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }
}
