package com.timeit.app.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.timeit.Database.TasksDAO
import com.timeit.app.Adapters.TasksAdapter
import com.timeit.app.DataModels.Day
import com.timeit.app.DataModels.TaskDataModel
import com.timeit.app.databinding.FragmentTaskBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var tasksList : MutableList<TaskDataModel>
    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var tasksDAO: TasksDAO
    private lateinit var selectedDate: Day
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedDate = arguments?.getSerializable("selectedDate") as? Day
            ?: throw IllegalArgumentException("Argument 'selectedDate' must be provided")

        tasksList = mutableListOf()
        tasksDAO = TasksDAO(requireContext())

        initializeAdapter(requireContext())

        loadTasksFromDatabase(selectedDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tasksDAO.close()
        _binding = null
    }

    fun updateTasks(tasksList: List<TaskDataModel>) {
        tasksAdapter.updateTasks(tasksList)
    }

    private fun initializeAdapter(inContext: Context) {
        tasksAdapter = TasksAdapter(tasksList, inContext)
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tasksRecyclerView.adapter = tasksAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadTasksFromDatabase(selectedDate: Day) {
        val calendar = Calendar.getInstance().apply {
            set(selectedDate.year, getMonthIndex(selectedDate.dayMonth), selectedDate.dayNumber)
        }
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        CoroutineScope(Dispatchers.Main).launch {
            tasksList.clear()
            tasksList.addAll(tasksDAO.getTasksForDate(formattedDate))
            if (tasksList.isEmpty()) {
                binding.tasksRecyclerView.visibility = View.GONE
                tasksAdapter.notifyDataSetChanged()
            } else {
                tasksAdapter.notifyDataSetChanged()
                binding.tasksRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun getMonthIndex(monthName: String): Int {
        val dateFormatSymbols = DateFormatSymbols.getInstance(Locale.getDefault())
        val monthNames = dateFormatSymbols.months
        return monthNames.indexOf(monthName)
    }

    companion object {
        @JvmStatic
        fun newInstance(selectedDate: Day) =
            TaskFragment().apply {
                arguments = Bundle().apply {
                    // Pass selectedDate to fragment arguments if needed
                    putSerializable("selectedDate", selectedDate)
                }
            }
    }
}