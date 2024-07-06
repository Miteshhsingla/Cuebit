package com.timeit.app.Fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.timeit.Database.TasksDAO
import com.timeit.app.AlarmReceiver
import com.timeit.app.DataModels.Frequency
import com.timeit.app.DataModels.TaskDataModel
import com.timeit.app.databinding.FragmentAddTaskBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddTaskFragment : Fragment() {
    private var _binding: FragmentAddTaskBinding? = null
    private lateinit var tasksDAO: TasksDAO
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var calendar: Calendar
    private val binding get() = _binding!!
    private lateinit var TaskDetails: TaskDataModel
    private lateinit var TaskTitle: String
    private lateinit var TaskDescription: String
    private lateinit var TaskCategory: String
    private lateinit var TaskDateTime: String
    private lateinit var TaskFrequency: String
    private lateinit var TaskId: String
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var frequencyAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tasksDAO = TasksDAO(activity)

        // Initialize Category Spinner Adapter
        categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.category.adapter = categoryAdapter

        // Fetch Category Names from Database
        lifecycleScope.launch {
            val categoryNames = tasksDAO.fetchAllCategories().map { it.categoryName }
            categoryAdapter.addAll(categoryNames)
            categoryAdapter.notifyDataSetChanged()
        }

        // Initialize Frequency Spinner Adapter And Showing Data in Spinner
        frequencyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, Frequency.entries.map { it.name })
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.Frequency.adapter = frequencyAdapter

        binding.dateAndTime.setOnClickListener {
            showDateTimePicker(binding.dateAndTime)
        }

        binding.SetReminderButton.setOnClickListener {
            setData()

            if (TaskDetails.id?.isNotEmpty() == true && TaskDetails.title?.isNotEmpty() == true && TaskDetails.description?.isNotEmpty() == true && TaskDetails.dateAndTime?.isNotEmpty() == true && TaskDetails.frequency?.isNotEmpty() == true && TaskDetails.category?.isNotEmpty() == true) {
                lifecycleScope.launch {
                    try {
                        tasksDAO.insertTask(TaskDetails)
                        Toast.makeText(activity, "Task Created Successfully", Toast.LENGTH_LONG).show()
                        // Send broadcast to notify task insertion
                        val intentHomeFragment = Intent("com.timeit.app.ACTION_TASK_INSERTED")
                        requireContext().sendBroadcast(intentHomeFragment)

                        // Dismiss bottom sheet
                        val intent = Intent("com.timeit.app.ACTION_DISMISS_BOTTOM_SHEET")
                        requireContext().sendBroadcast(intent)
                    } catch (e: Exception) {
                        Log.e("AddTaskFragment", "Error inserting task", e)
                        Toast.makeText(activity, "Failed to create task", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(activity, "Please fill all the empty fields", Toast.LENGTH_LONG).show()
            }

            setAlarm()
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm() {
        alarmManager = requireContext().getSystemService(ALARM_SERVICE) as AlarmManager

        // Parse the date and time from the EditText
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateTime = dateTimeFormat.parse(TaskDateTime) ?: return
        calendar = Calendar.getInstance().apply {
            time = dateTime
        }

        val intent = Intent(activity, AlarmReceiver::class.java).apply {
            putExtra("TASK_TITLE", TaskTitle)
        }
        pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(activity, "Alarm set successfully", Toast.LENGTH_LONG).show()
    }

    private fun setData() {
        TaskId = generateId()

        TaskTitle = binding.title.text.toString()
        TaskDescription = binding.Description.text.toString()
        TaskCategory = binding.category.selectedItem.toString()
        TaskDateTime = binding.dateAndTime.text.toString()
        TaskFrequency = binding.Frequency.selectedItem.toString()

        TaskDetails = TaskDataModel(TaskId, TaskTitle, TaskDescription, TaskCategory, TaskDateTime, TaskFrequency)
    }

    private fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    private fun showDateTimePicker(editText: EditText) {

        binding.dateAndTime.showSoftInputOnFocus = false
        binding.dateAndTime.isClickable = true

        calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        editText.setText(dateTimeFormat.format(calendar.time))
                        binding.dateAndTime.showSoftInputOnFocus = true
                        binding.dateAndTime.isClickable = true
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddTaskFragment()
    }
}
