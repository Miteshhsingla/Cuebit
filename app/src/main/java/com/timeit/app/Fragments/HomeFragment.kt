package com.timeit.app.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.timeit.app.Adapters.DateAdapter
import com.timeit.app.DataModels.Day
import com.timeit.app.Utils
import com.timeit.app.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    val Utils = Utils()
    private val binding get() = _binding!!
    private lateinit var dateAdapter: DateAdapter
    private var selectedDayPosition = -1
    val dates = Utils.generateDaysForMonth()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateAdapter = DateAdapter(dates)
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = dateAdapter
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
        dateAdapter.updateSelected(today)

        val todayPosition = getTodayPosition(dates)
        binding.recyclerView.scrollToPosition(todayPosition)

        dateAdapter.setOnItemClickListener { position ->
            selectedDayPosition = position
            dateAdapter.updateSelected(position)
        }
        setCurrentMonth()
    }

    private fun getTodayPosition(dates: List<Day>): Int {
        return dates.indexOfFirst { it.isToday }
    }
    private fun setCurrentMonth() {
        val calendar = Calendar.getInstance()
        val monthYearFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val monthYearText = monthYearFormat.format(calendar.time)
        binding.selectedDayText.text = monthYearText
    }








    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}