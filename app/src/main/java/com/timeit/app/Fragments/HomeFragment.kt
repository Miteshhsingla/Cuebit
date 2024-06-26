package com.timeit.app.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import com.timeit.app.R
import com.timeit.app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tabMode.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val fragment = when (tab?.position) {
                    0 -> TaskFragment.newInstance()
                    1 -> HabitsFragment.newInstance()
                    else -> null
                }
                fragment?.let {
                    val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, it)
                    transaction.commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        if (savedInstanceState == null) {
            val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, TaskFragment.newInstance())
            transaction.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}