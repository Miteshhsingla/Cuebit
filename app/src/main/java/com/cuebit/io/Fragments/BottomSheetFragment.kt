package com.cuebit.io.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.cuebit.app.Fragments.AddHabitsFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.cuebit.io.R
import com.cuebit.io.databinding.FragmentBottomSheetBinding

class BottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val dismissReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabMode: TabLayout = binding.tabModeFAB
        OnTabSelection(tabMode)

        binding.crossButton.setOnClickListener{
            dismiss()
        }

        if (savedInstanceState == null) {
            val defaultFragment = AddTaskFragment.newInstance()
            val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerBottomSheet, defaultFragment)
            transaction.commit()
        }

        view.post {
            val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = false
            }
        }
    }

    fun OnTabSelection(tabMode:TabLayout){
        try {
            tabMode.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val fragment = when (tab?.position) {
                        0 -> AddTaskFragment.newInstance()
                        1 -> AddHabitsFragment.newInstance()
                        else -> null
                    }

                    fragment?.let {
                        val containerView = binding.fragmentContainerBottomSheet
                        try {
                            if (containerView != null) {
                                try {
                                    val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
                                    transaction.replace(R.id.fragmentContainerBottomSheet, it)
                                    transaction.addToBackStack(null)
                                    transaction.commit()
                                } catch (e: Exception) {
                                    Log.e("BottomSheetFragment", "Error replacing fragment: ${e.message}", e)
                                }
                            } else {
                                Log.e("BottomSheetFragment", "Fragment container not found")
                            }
                        } catch (e: Exception) {
                            Log.e("BottomSheetFragment", "Error: ${e.message}", e)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        } catch (e: Exception) {
            Log.e("BottomSheetFragment", "Error: ${e.message}", e)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("com.cuebit.io.ACTION_DISMISS_BOTTOM_SHEET")
        requireContext().registerReceiver(dismissReceiver, filter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireContext().unregisterReceiver(dismissReceiver)

    }
}
