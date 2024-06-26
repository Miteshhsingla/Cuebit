package com.timeit.app.Fragments

import android.app.DatePickerDialog
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
    private var currentWeekStart: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateAdapter = DateAdapter(generateWeekDays(currentWeekStart))
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = dateAdapter

        binding.leftArrow.setOnClickListener { showPreviousWeek() }
        binding.rightArrow.setOnClickListener { showNextWeek() }

        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
        dateAdapter.updateSelected(today)

        val todayPosition = getTodayPosition(dateAdapter.dateItemList)
        selectedDayPosition = todayPosition
        dateAdapter.updateSelected(todayPosition)
        binding.recyclerView.scrollToPosition(todayPosition)

        dateAdapter.setOnItemClickListener { position ->
            selectedDayPosition = position
            dateAdapter.updateSelected(position)
            val selectedDate = dateAdapter.dateItemList[position]
            binding.selectedDayText.text = "${selectedDate.dayMonth} ${selectedDate.year}"
        }

        binding.monthtext.setOnClickListener {
            showDatePickerDialog()
        }

        setCurrentMonth(currentWeekStart)
    }

    private fun generateWeekDays(startingDay: Calendar): List<Day> {
        return Utils.generateDaysForWeek(startingDay)
    }

    private fun showPreviousWeek() {
        currentWeekStart.add(Calendar.WEEK_OF_YEAR, -1)
        updateWeek()
    }

    private fun showNextWeek() {
        currentWeekStart.add(Calendar.WEEK_OF_YEAR, 1)
        updateWeek()
    }

    private fun updateWeek() {
        dateAdapter.updateData(generateWeekDays(currentWeekStart))
        binding.recyclerView.scrollToPosition(0)
        setCurrentMonth(currentWeekStart)
    }
    private fun getTodayPosition(dates: List<Day>): Int {
        return dates.indexOfFirst { it.isToday }
    }
    private fun setCurrentMonth(calendar: Calendar) {
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthYearText = monthYearFormat.format(calendar.time)
        binding.selectedDayText.text = monthYearText
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
            currentWeekStart = selectedCalendar
            updateWeek()
            val dateText = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
            binding.selectedDayText.text = dateText
        }, year, month, day)

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}