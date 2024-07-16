package com.cuebit.app.Fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.cuebit.Database.TasksDAO
import com.cuebit.io.AlarmReceiver
import com.cuebit.io.DataModels.Frequency
import com.cuebit.io.DataModels.HabitDataModel
import com.cuebit.io.R
import com.cuebit.io.databinding.FragmentAddHabitsBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddHabitsFragment : Fragment() {
    private var _binding: FragmentAddHabitsBinding? = null
    private val binding get() = _binding!!
    private lateinit var tasksDAO: TasksDAO
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var calendar: Calendar
    private lateinit var frequencyAdapter: ArrayAdapter<String>
    private lateinit var HabitDetails: HabitDataModel
    private lateinit var HabitTitle: String
    private lateinit var HabitDescription: String
    private lateinit var HabitDate: String
    private lateinit var HabitFrequency: String
    private lateinit var HabitId: String
    private lateinit var HabitTime: String
    private lateinit var HabitIsCompleted: String
    private lateinit var HabitGoal: String
    private var HabitImageIcon: Int = 0


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

        tasksDAO = TasksDAO(activity)

        // Initialize Frequency Spinner Adapter And Showing Data in Spinner
        frequencyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, Frequency.entries.map { it.name })
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.habitFrequency.adapter = frequencyAdapter

        binding.habitDate.setOnClickListener {
            showDatePicker(binding.habitDate)
        }

        binding.habitTime.setOnClickListener {
            showTimePicker(binding.habitTime)
        }

        binding.SetReminderButton.setOnClickListener {
            setData()

            if (HabitDetails.id?.isNotEmpty() == true && HabitDetails.habitName?.isNotEmpty() == true && HabitDetails.description?.isNotEmpty() == true && HabitDetails.startDate?.isNotEmpty() == true && HabitDetails.frequency?.isNotEmpty() == true) {
                lifecycleScope.launch {
                    try {
                        tasksDAO.insertHabit(HabitDetails)
                        Toast.makeText(activity, "Habit Created Successfully", Toast.LENGTH_LONG).show()
                        // Send broadcast to notify task insertion
                        val intentHomeFragment = Intent("com.cuebit.app.ACTION_HABIT_INSERTED")
                        requireContext().sendBroadcast(intentHomeFragment)

                        // Dismiss bottom sheet
                        val intent = Intent("com.cuebit.app.ACTION_DISMISS_BOTTOM_SHEET")
                        requireContext().sendBroadcast(intent)
                    } catch (e: Exception) {
                        Log.e("AddHabitsFragment", "Error inserting habit", e)
                        Toast.makeText(activity, "Failed to create habit", Toast.LENGTH_LONG).show()
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
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Parse the date and time from the EditText
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateTime = dateTimeFormat.parse(HabitDate) ?: return
        calendar = Calendar.getInstance().apply {
            time = dateTime
        }

        val intent = Intent(activity, AlarmReceiver::class.java).apply {
            putExtra("TASK_TITLE", HabitTitle)
        }
        pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(activity, "Alarm set successfully", Toast.LENGTH_LONG).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData() {
        HabitId = generateId()

        HabitTitle = binding.habitTitle.text.toString()
        HabitDescription = binding.habitDescription.text.toString()
        HabitDate = binding.habitDate.text.toString()
        HabitTime = binding.habitTime.text.toString()
        HabitGoal = binding.habitGoal.text.toString()
        HabitIsCompleted = "0"
        HabitImageIcon = R.drawable.avatar1
        HabitFrequency = binding.habitFrequency.selectedItem.toString()

        HabitDetails = HabitDataModel(HabitId, HabitTitle, HabitDescription, HabitFrequency, HabitIsCompleted, HabitTime, HabitImageIcon, HabitGoal, HabitDate)
    }

    private fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    private fun showDatePicker(editText: EditText) {

        binding.habitDate.showSoftInputOnFocus = false
        binding.habitDate.isClickable = true

        calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Format the selected date and set it to the EditText
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                editText.setText(dateFormat.format(calendar.time))
                binding.habitDate.showSoftInputOnFocus = true
                binding.habitDate.isClickable = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun showTimePicker(editText: EditText) {
        binding.habitTime.showSoftInputOnFocus = false
        binding.habitTime.isClickable = true

        val calendar = Calendar.getInstance()

        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // Format the selected time and set it to the EditText
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                editText.setText(timeFormat.format(calendar.time))
                binding.habitTime.showSoftInputOnFocus = true
                binding.habitTime.isClickable = true
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        timePicker.show()
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