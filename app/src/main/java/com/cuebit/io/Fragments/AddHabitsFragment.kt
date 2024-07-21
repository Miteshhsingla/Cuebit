package com.cuebit.app.Fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuebit.io.Adapters.HabitsAdapter
import com.cuebit.io.Adapters.HabitsListAdapter
import com.cuebit.io.DataModels.Habits
import com.cuebit.io.databinding.FragmentAddHabitsBinding

class AddHabitsFragment : Fragment() {
    private var _binding: FragmentAddHabitsBinding? = null
    private val binding get() = _binding!!
    private lateinit var habitsListAdapter: HabitsListAdapter
    private lateinit var recyclerView : RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddHabitsBinding.inflate(inflater, container, false);
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerHabitsList

        recyclerView.layoutManager = LinearLayoutManager(context)
        habitsListAdapter = HabitsListAdapter(requireContext(),Habits.habits)
        recyclerView.adapter = habitsListAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddHabitsFragment()
    }
}