package com.timeit.app.Fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.timeit.app.Adapters.HabitsAdapter
import com.timeit.app.DataModels.HabitDataModel
import com.timeit.app.DataModels.Habits
import com.timeit.app.MainActivity
import com.timeit.app.databinding.FragmentHabitsBinding

class HabitsFragment : Fragment() {

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    private lateinit var habitsAdapter: HabitsAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    private val habitslist: List<HabitDataModel> = Habits.habits

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        habitsAdapter = HabitsAdapter(habitslist, requireContext())
        binding.habitRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.habitRecyclerView.adapter = habitsAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = HabitsFragment()
    }
}