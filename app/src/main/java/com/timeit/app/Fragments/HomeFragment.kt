package com.timeit.app.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.timeit.app.Adapters.DateAdapter
import com.timeit.app.DataModels.Day
import com.timeit.app.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dateAdapter: DateAdapter
    private var selectedDayPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateAdapter = DateAdapter(getDateItemList())
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = dateAdapter
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
        dateAdapter.updateSelected(today)
        dateAdapter.setOnItemClickListener { position ->
            selectedDayPosition = position
            dateAdapter.updateSelected(position)
        }
        setCurrentMonth()
    }

    private fun setCurrentMonth() {
        val calendar = Calendar.getInstance()
        val monthYearFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val monthYearText = monthYearFormat.format(calendar.time)
        binding.selectedDayText.text = monthYearText
    }

    private fun getDateItemList(): List<Day> {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.get(Calendar.DAY_OF_MONTH)
        val weekDays = mutableListOf<Day>()

        calendar.set(Calendar.DAY_OF_MONTH, currentDate)

        for (i in 0..6) {
            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
            val isToday = i == 0

            weekDays.add(Day(dayOfWeek, dayNumber, isToday))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return weekDays
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}