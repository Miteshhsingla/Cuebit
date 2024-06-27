package com.timeit.app.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.timeit.app.R
import com.timeit.app.databinding.FragmentBottomSheetBinding

class BottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

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
        try {
            tabMode.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val fragment = when (tab?.position) {
                        0 -> TaskFragment.newInstance()
                        1 -> HabitsFragment.newInstance()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
