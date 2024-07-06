package com.timeit.app.Fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.BroadcastReceiver
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timeit.Database.TasksDAO
import com.timeit.Utils.Utils
import com.timeit.app.Adapters.CategoryAdapter
import com.timeit.app.Adapters.DateAdapter
import com.timeit.app.Adapters.TasksAdapter
import com.timeit.app.DataModels.Category
import com.timeit.app.DataModels.Day
import com.timeit.app.DataModels.TaskDataModel
import com.timeit.app.R
import com.timeit.app.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class HomeFragment : Fragment(), CategoryAdapter.OnTasksFetchedListener {
    private var binding: FragmentHomeBinding? = null
    private val utils = Utils()
    private var dateAdapter: DateAdapter? = null
    private var selectedDayPosition = -1
    private var selectedDate: Day? = null
    private var currentWeekStart = Calendar.getInstance()
    private var tasksList: MutableList<TaskDataModel>? = null
    private var tasksDAO: TasksDAO? = null
    private var tasksAdapter: TasksAdapter? = null
    private lateinit var categoryAdapter : CategoryAdapter
    private lateinit var tasksrecycler: RecyclerView
    private lateinit var categoryList : MutableList<Category>
    private var userName: String = ""
    private var userImage: Int = 0

    private val taskInsertedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Load tasks again when a new task is inserted
            selectedDate?.let { loadTasksFromDatabase(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetching name stored in shared preference
        val sharedPreferences = requireContext().getSharedPreferences("com.timeit.app", Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            userName = sharedPreferences.getString("userName", "").toString()
            userImage = sharedPreferences.getInt("selectedAvatar",0)
            binding?.greetingUsername?.text = "Hey, " + userName + " 👋"
            binding?.userImage?.setImageResource(userImage)
        }

        // Set currentWeekStart to the start of the week (Monday)
        while (currentWeekStart.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            currentWeekStart.add(Calendar.DAY_OF_MONTH, -1)
        }

        // Calendar Adapter setup
        selectedDate = utils.getDayFromDate(Calendar.getInstance())
        dateAdapter = DateAdapter(generateWeekDays(currentWeekStart))
        binding!!.recyclerView.layoutManager = GridLayoutManager(context, 7)
        binding!!.recyclerView.adapter = dateAdapter

        // Initialize taskList and taskDao
        tasksList = mutableListOf()
        tasksDAO = TasksDAO(requireContext())

        // Calendar arrows for scrolling weeks
        binding!!.leftArrow.setOnClickListener { v -> showPreviousWeek() }
        binding!!.rightArrow.setOnClickListener { v -> showNextWeek() }

        // To show today's date in the calendar
        val today = Calendar.getInstance()[Calendar.DAY_OF_WEEK] - 1
        dateAdapter!!.updateSelected(today)
        val todayPosition = getTodayPosition(dateAdapter!!.dateItemList)
        selectedDayPosition = todayPosition
        dateAdapter!!.updateSelected(todayPosition)
        binding!!.recyclerView.scrollToPosition(todayPosition)

        // Set adapter for showing tasks in RecyclerView
        tasksrecycler = binding!!.recylerViewTasks

        tasksAdapter = TasksAdapter(tasksList!!, requireContext())
        binding!!.recylerViewTasks.layoutManager = LinearLayoutManager(context)
        binding!!.recylerViewTasks.adapter = tasksAdapter
        setupSwipeToDelete()


        // On click for clicking on date in the calendar
        dateAdapter!!.setOnItemClickListener { position: Int ->
            selectedDayPosition = position
            dateAdapter!!.updateSelected(position)
            selectedDate = dateAdapter!!.dateItemList[position]
            binding!!.selectedDayText.text = String.format(
                "%s %s",
                selectedDate!!.dayMonth,
                selectedDate!!.year
            )
            loadTasksFromDatabase(selectedDate!!)
        }

        // On clicking month name
        binding!!.monthtext.setOnClickListener {
            showDatePickerDialog()
        }

        // Method to show current month by default
        setCurrentMonth(currentWeekStart)
//        loadTasksFromDatabase(selectedDate)

        //Initialize Category Adapter
        categoryList = mutableListOf()
        CoroutineScope(Main).launch {
            if (!tasksDAO!!.isCategoryExist("All")) {
                tasksDAO!!.addCategory("All", generateId())
            }
            categoryList = tasksDAO!!.fetchAllCategories()
            if (categoryList.isNotEmpty()) {
                setCategoryAdapter(categoryList)
            }
        }

        binding!!.AddCategory.setOnClickListener{
            utils.showAddCategoryDialog(requireContext(), categoryAdapter)
        }

        // Initialize Spinner
        val spinner = binding!!.taskType
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.task_types, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                loadTasksFromDatabase(selectedDate!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    override fun onTasksFetched(tasks: List<TaskDataModel>) {
        tasksAdapter?.updateTasks(tasks)
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val taskId = tasksAdapter?.getItem(position)?.id ?: return

                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        // Mark task as done
                        markTaskAsDone(position, taskId)
                    }
                    ItemTouchHelper.LEFT -> {
                        // Show delete confirmation dialog
                        showDeleteConfirmationDialog(position, taskId)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val icon: Int
                val background: ColorDrawable
                val itemView = viewHolder.itemView

                if (dX > 0) {
                    // Swiping right for "Mark as Done"
                    icon = R.drawable.ic_tick // Use your check icon here
                    background = ColorDrawable(Color.GREEN)
                } else {
                    // Swiping left for "Delete"
                    icon = R.drawable.delete // Use your delete icon here
                    background = ColorDrawable(Color.RED)
                }

                val iconDrawable = ContextCompat.getDrawable(requireContext(), icon)!!
                val iconMargin = (itemView.height - iconDrawable.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - iconDrawable.intrinsicHeight) / 2
                val iconBottom = iconTop + iconDrawable.intrinsicHeight

                when {
                    dX > 0 -> { // Swiping to the right
                        background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                        iconDrawable.setBounds(
                            itemView.left + iconMargin,
                            iconTop,
                            itemView.left + iconMargin + iconDrawable.intrinsicWidth,
                            iconBottom
                        )
                    }
                    dX < 0 -> { // Swiping to the left
                        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                        iconDrawable.setBounds(
                            itemView.right - iconDrawable.intrinsicWidth - iconMargin,
                            iconTop,
                            itemView.right - iconMargin,
                            iconBottom
                        )
                    }
                    else -> { // No swiping
                        background.setBounds(0, 0, 0, 0)
                        iconDrawable.setBounds(0, 0, 0, 0)
                    }
                }

                background.draw(c)
                iconDrawable.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(tasksrecycler)
    }

    private fun markTaskAsDone(position: Int, taskId: String) {
        CoroutineScope(Main).launch {
            tasksDAO?.markTaskAsDone(taskId)
            tasksAdapter?.removeItem(position)  // Or update the item status and notify changes
            Toast.makeText(activity, "Task marked as done", Toast.LENGTH_SHORT).show()
        }
    }



    private fun showDeleteConfirmationDialog(position: Int,taskId:String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { dialog, _ ->
                tasksAdapter?.removeItem(position)
                CoroutineScope(Main).launch {
                tasksDAO?.deleteTask(taskId)
                    Toast.makeText(activity,"Task Deleted Successfully",Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                tasksAdapter?.notifyItemChanged(position)
                dialog.dismiss()
            }
        builder.create().show()
    }


    private fun generateWeekDays(startingDay: Calendar): List<Day> {
        return utils.generateDaysForWeek(startingDay)
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

        // Adjust currentWeekStart to the start of the week (Monday)
        while (currentWeekStart.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            currentWeekStart.add(Calendar.DAY_OF_MONTH, -1)
        }

        dateAdapter!!.updateData(generateWeekDays(currentWeekStart))
        binding!!.recyclerView.scrollToPosition(0)
        setCurrentMonth(currentWeekStart)
    }

    private fun getTodayPosition(dates: List<Day>): Int {
        return dates.indexOfFirst(Day::isToday)
    }

    private fun setCurrentMonth(calendar: Calendar) {
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthYearText = monthYearFormat.format(calendar.time)
        binding!!.selectedDayText.text = monthYearText
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(requireContext(),
            { view: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                // Adjust selectedCalendar to the start of the week (Monday)
                while (selectedCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                    selectedCalendar.add(Calendar.DAY_OF_MONTH, -1)
                }

                currentWeekStart = selectedCalendar
                updateWeek()

                // Update the selected date and load tasks for that date
                val selectedDate = utils.getDayFromDate(selectedCalendar)
                this.selectedDate = selectedDate

                // Highlight the selected date in the DateAdapter
                val position = dateAdapter?.dateItemList?.indexOfFirst {
                    it.dayNumber == selectedDay && it.dayMonth == DateFormatSymbols().months[selectedMonth] && it.year == selectedYear
                } ?: -1
                if (position != -1) {
                    dateAdapter?.updateSelected(position)
                    binding?.recyclerView?.scrollToPosition(position)
                }

                loadTasksFromDatabase(selectedDate)

                // Update the month text
                val dateText = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedCalendar.time)
                binding?.selectedDayText?.text = dateText
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun loadTasksFromDatabase(selectedDate: Day) {
        val calendar = Calendar.getInstance()
        calendar[selectedDate.year, getMonthIndex(selectedDate.dayMonth)] = selectedDate.dayNumber
        val formattedDate =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        val selectedType = binding?.taskType?.selectedItem.toString()
        val scope: CoroutineScope = CoroutineScope(Main)
        scope.launch {
            tasksList!!.clear()
            if ("Habits" == selectedType) {
//                tasksList!!.addAll(tasksDAO.getHabitsForDate(formattedDate))
            } else {
                tasksList!!.addAll(tasksDAO!!.getTasksForDate(formattedDate))
            }
            if(tasksList!!.isNotEmpty()){
                binding!!.emptyState.isVisible = false
                binding!!.recylerViewTasks.isVisible = true
                tasksAdapter!!.notifyDataSetChanged()
            }
            else{
                tasksAdapter!!.notifyDataSetChanged()
                binding!!.recylerViewTasks.isVisible = false
                binding!!.emptyState.isVisible = true
            }
//            if (tasksList!!.isEmpty()) {
//                binding!!.recylerViewTasks.visibility = View.GONE
//
//            } else {
//                binding!!.recylerViewTasks.visibility = View.VISIBLE
//            }
        }
    }

    private fun getMonthIndex(monthName: String): Int {
        val dateFormatSymbols = DateFormatSymbols.getInstance(Locale.getDefault())
        val monthNames = dateFormatSymbols.months
        for (i in monthNames.indices) {
            if (monthNames[i].equals(monthName, ignoreCase = true)) {
                return i
            }
        }
        return -1 // In case the month name is not found
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("com.timeit.app.ACTION_TASK_INSERTED")
        requireContext().registerReceiver(taskInsertedReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(taskInsertedReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tasksDAO?.close()
        binding = null
    }

    private fun setCategoryAdapter(categoryList : MutableList<Category>){
        categoryAdapter = CategoryAdapter(categoryList, tasksDAO!!, requireContext(),this)
        binding!!.categoryRecyclerView.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        binding!!.categoryRecyclerView.adapter = categoryAdapter
    }
}
