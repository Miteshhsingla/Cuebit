package com.timeit.app.Fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.timeit.Database.TasksDAO
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

    private val binding get() = _binding!!
    private lateinit var TaskDetails: TaskDataModel
    private lateinit var TaskTitle : String
    private lateinit var TaskDescription : String
    private lateinit var TaskCategory : String
    private lateinit var TaskDateTime : String
    private lateinit var TaskFrequency : String
    private lateinit var TaskId : String

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

        binding.dateAndTime.setOnClickListener{
            showDateTimePicker(binding.dateAndTime);
        }


        binding.SetReminderButton.setOnClickListener{
            setData();

            lifecycleScope.launch {
                tasksDAO.insertTask(TaskDetails)
            }
            Toast.makeText(activity,"Task Created Successfully",Toast.LENGTH_LONG).show()
        }

    }

    private fun setData() {
        TaskId = generateId()

        TaskTitle =  binding.title.text.toString()
        TaskDescription = binding.Description.text.toString()
        TaskCategory = binding.category.toString()
        TaskDateTime = binding.dateAndTime.text.toString()
        TaskFrequency = binding.Frequency.text.toString()

        TaskDetails = TaskDataModel(TaskId, TaskTitle, TaskDescription, TaskCategory, TaskDateTime, TaskFrequency)
    }

    private fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    private fun showDateTimePicker(editText: EditText) {

        binding.dateAndTime.showSoftInputOnFocus = false
        binding.dateAndTime.isClickable = true

        val calendar = Calendar.getInstance()

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

    companion object {
        @JvmStatic
        fun newInstance() = AddTaskFragment()
    }
}