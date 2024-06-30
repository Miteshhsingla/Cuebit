package com.timeit.app.Fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.timeit.Database.TasksDAO
import com.timeit.app.DataModels.TaskDataModel
import com.timeit.app.databinding.FragmentAddTaskBinding
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

            if(TaskDetails != null){
                tasksDAO.insertTask(TaskDetails);
            }
        }
    }

    private fun setData() {
        TaskId = generateId()

        TaskTitle =  binding.title.toString()
        TaskDescription = binding.Description.toString()
        TaskCategory = binding.category.toString()
        TaskDateTime = binding.dateAndTime.toString()
        TaskFrequency = binding.Frequency.toString()

        TaskDetails = TaskDataModel("a","aa","aaa","aaaa","aaaaa")
    }

    fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    private fun showDateTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()

        val datePicker = activity?.let {
            DatePickerDialog(
                it,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    TimePickerDialog(
                        activity,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)

                            val dateTimeFormat =
                                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            editText.setText(dateTimeFormat.format(calendar.time))
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
        }

        if (datePicker != null) {
            datePicker.show()
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() = AddTaskFragment()
    }
}