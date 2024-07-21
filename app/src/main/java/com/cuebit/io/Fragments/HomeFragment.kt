package com.cuebit.io.Fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.BroadcastReceiver
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuebit.Database.TasksDAO
import com.cuebit.Utils.Utils
import com.cuebit.io.Adapters.CategoryAdapter
import com.cuebit.io.Adapters.DateAdapter
import com.cuebit.io.Adapters.HabitsAdapter
import com.cuebit.io.Adapters.TasksAdapter
import com.cuebit.io.DataModels.Category
import com.cuebit.io.DataModels.Day
import com.cuebit.io.DataModels.HabitDataModel
import com.cuebit.io.DataModels.TaskDataModel
import com.google.android.material.tabs.TabLayout
import com.cuebit.io.R
import com.cuebit.io.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private var habitsList: MutableList<HabitDataModel>? = null
    private var tasksDAO: TasksDAO? = null
    private var tasksAdapter: TasksAdapter? = null
    private var habitsAdapter: HabitsAdapter? = null
    private lateinit var categoryAdapter : CategoryAdapter
    private lateinit var tasks_habits_recycler: RecyclerView
    private lateinit var categoryList : MutableList<Category>
    private var userName: String = ""
    private var userImage: Int = 0
    private lateinit var tabMode: TabLayout

    private val taskInsertedReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetching name stored in shared preference
        val sharedPreferences = requireContext().getSharedPreferences("com.cuebit.io", Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            userName = sharedPreferences.getString("userName", "").toString()
            userImage = sharedPreferences.getInt("selectedAvatar", 0)
            binding?.greetingUsername?.text = "Hey, $userName 👋"
            binding?.userImage?.setImageResource(userImage)
        }

        // Set currentWeekStart to the start of the week (Monday)
        while (currentWeekStart.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            currentWeekStart.add(Calendar.DAY_OF_MONTH, -1)
        }

        // Calendar Adapter setup
        selectedDate = utils.getDayFromDate(Calendar.getInstance())
        dateAdapter = DateAdapter(generateWeekDays(currentWeekStart))
        binding?.recyclerView?.layoutManager = GridLayoutManager(context, 7)
        binding?.recyclerView?.adapter = dateAdapter

        // Initialize taskList and taskDao
        tasksList = mutableListOf()
        habitsList = mutableListOf()  // Initialize habitsList here
        tasksDAO = TasksDAO(requireContext())

        // Calendar arrows for scrolling weeks
        binding?.leftArrow?.setOnClickListener { showPreviousWeek() }
        binding?.rightArrow?.setOnClickListener { showNextWeek() }

        // To show today's date in the calendar
        val today = Calendar.getInstance()[Calendar.DAY_OF_WEEK] - 1
        dateAdapter?.updateSelected(today)
        val todayPosition = getTodayPosition(dateAdapter?.dateItemList ?: emptyList())
        selectedDayPosition = todayPosition
        dateAdapter?.updateSelected(todayPosition)
        binding?.recyclerView?.scrollToPosition(todayPosition)

        // Initialize Spinner
        tabMode = binding!!.tabModeFAB
        onTabSelection(tabMode)

        // Set adapter for showing tasks and habits in RecyclerView
        tasks_habits_recycler = binding?.recylerViewTasks ?: RecyclerView(requireContext())

        setupRecyclerViewAdapter()
        setupSwipeToDelete()

        // On click for clicking on date in the calendar
        dateAdapter?.setOnItemClickListener { position: Int ->
            selectedDayPosition = position
            dateAdapter?.updateSelected(position)
            selectedDate = dateAdapter?.dateItemList?.get(position)
            binding?.selectedDayText?.text = String.format(
                "%s %s",
                selectedDate?.dayMonth,
                selectedDate?.year
            )
            selectedDate?.let { loadTasksFromDatabase(it) }
        }

        // On clicking month name
        binding?.monthtext?.setOnClickListener {
            showDatePickerDialog()
        }

        // Method to show current month by default
        setCurrentMonth(currentWeekStart)

        // Initialize Category Adapter
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

        binding?.AddCategory?.setOnClickListener {
            utils.showAddCategoryDialog(requireContext(), categoryAdapter)
        }

        selectedDate?.let { loadTasksFromDatabase(it) }
    }

    private fun onTabSelection(tabMode: TabLayout) {
        try {
            tabMode.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> { // Tasks Tab
                            selectedDate?.let { loadTasksFromDatabase(it) }
                        }
                        1 -> { // Habits Tab
                            selectedDate?.let { loadTasksFromDatabase(it) }
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error: ${e.message}", e)
        }
    }

    private fun setupRecyclerViewAdapter() {
        when (getSelectedTabType()) {
            TASKS_TAB -> {
                tasksAdapter = TasksAdapter(tasksList ?: mutableListOf(), requireContext())
                binding?.recylerViewTasks?.layoutManager = LinearLayoutManager(context)
                binding?.recylerViewTasks?.adapter = tasksAdapter
            }
            HABITS_TAB -> {
                habitsAdapter = HabitsAdapter(habitsList ?: mutableListOf(), requireContext())
                binding?.recylerViewTasks?.layoutManager = LinearLayoutManager(context)
                binding?.recylerViewTasks?.adapter = habitsAdapter
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

                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        // Mark task and habit as done
                        when (getSelectedTabType()) {
                            TASKS_TAB -> {
                                if (position >= 0 && position < (tasksAdapter?.itemCount ?: 0)) {
                                    val taskId = tasksAdapter?.getItem(position)?.id ?: return
                                    markAsDone(position, taskId, "Tasks")
                                }
                            }
                            HABITS_TAB -> {
                                if (position >= 0 && position < (habitsAdapter?.itemCount ?: 0)) {
                                    val habitId = habitsAdapter?.getItem(position)?.id ?: return
                                    markAsDone(position, habitId, "Habits")
                                }
                            }
                        }
                    }
                    ItemTouchHelper.LEFT -> {
                        // Show delete confirmation dialog
                        when (getSelectedTabType()) {
                            TASKS_TAB -> {
                                if (position >= 0 && position < (tasksAdapter?.itemCount ?: 0)) {
                                    val taskId = tasksAdapter?.getItem(position)?.id ?: return
                                    showDeleteConfirmationDialog(position, taskId, "Tasks")
                                }
                            }
                            HABITS_TAB -> {
                                if (position >= 0 && position < (habitsAdapter?.itemCount ?: 0)) {
                                    val habitId = habitsAdapter?.getItem(position)?.id ?: return
                                    showDeleteConfirmationDialog(position, habitId, "Habits")
                                }
                            }
                        }
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
                    icon = R.drawable.ic_tick
                    background = ColorDrawable(Color.GREEN)
                } else {
                    // Swiping left for "Delete"
                    icon = R.drawable.delete
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
        itemTouchHelper.attachToRecyclerView(tasks_habits_recycler)
    }

    private fun markAsDone(position: Int, id: String, selectedType: String) {
        CoroutineScope(Main).launch {
            if(selectedType == "Tasks"){
                tasksDAO?.markTaskAsDone(id)
                tasksAdapter?.removeItem(position)  // Or update the item status and notify changes
                Toast.makeText(activity, "Task marked as done", Toast.LENGTH_SHORT).show()
            } else {
                tasksDAO?.markHabitAsDone(id)
                habitsAdapter?.removeItem(position)  // Or update the item status and notify changes
                Toast.makeText(activity, "Habit marked as done", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(position: Int, id:String, type: String) {
        val builder = AlertDialog.Builder(requireContext())
        if(type == "Tasks"){
            builder.setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes") { dialog, _ ->
                    tasksAdapter?.removeItem(position)
                    CoroutineScope(Main).launch {
                        tasksDAO?.deleteTask(id)
                        Toast.makeText(activity, "Task Deleted Successfully", Toast.LENGTH_LONG)
                            .show()
                    }
                    dialog.dismiss()
                    if (tasksList!!.isEmpty()) {
                        binding!!.emptyState.isVisible = true
                        binding!!.recylerViewTasks.isVisible = false
                    } else {
                        binding!!.emptyState.isVisible = false
                        binding!!.recylerViewTasks.isVisible = true
                    }
                }
                .setNegativeButton("No") { dialog, _ ->
                    tasksAdapter?.notifyItemChanged(position)
                    dialog.dismiss()
                }
            builder.create().show()
        } else if(type == "Habits") {
            builder.setMessage("Are you sure you want to delete this habit?")
                .setPositiveButton("Yes") { dialog, _ ->
                    habitsAdapter?.removeItem(position)
                    CoroutineScope(Main).launch {
                        tasksDAO?.deleteHabit(id)
                        Toast.makeText(activity, "Habit Deleted Successfully", Toast.LENGTH_LONG)
                            .show()
                    }
                    dialog.dismiss()
                    if (habitsList!!.isEmpty()) {
                        binding!!.emptyState.isVisible = true
                        binding!!.recylerViewTasks.isVisible = false
                    } else {
                        binding!!.emptyState.isVisible = false
                        binding!!.recylerViewTasks.isVisible = true
                    }
                }
                .setNegativeButton("No") { dialog, _ ->
                    habitsAdapter?.notifyItemChanged(position)
                    dialog.dismiss()
                }
            builder.create().show()
        }
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

    private fun getSelectedTabType(): Int {
        return tabMode.selectedTabPosition
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadTasksFromDatabase(selectedDate: Day) {
        val calendar = Calendar.getInstance()
        calendar[selectedDate.year, getMonthIndex(selectedDate.dayMonth)] = selectedDate.dayNumber
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        val scope: CoroutineScope = CoroutineScope(Main)
        scope.launch {
            withContext(Dispatchers.IO) {
                tasksList!!.clear()
                habitsList!!.clear()
                when (getSelectedTabType()) {
                    HABITS_TAB -> habitsList!!.addAll(tasksDAO!!.getHabitsForDate(formattedDate))
                    TASKS_TAB -> tasksList!!.addAll(tasksDAO!!.getTasksForDate(formattedDate))
                    else -> {}
                }
            }
            setupRecyclerViewAdapter()
            when (getSelectedTabType()) {
                HABITS_TAB -> habitsAdapter?.notifyDataSetChanged()
                TASKS_TAB -> tasksAdapter?.notifyDataSetChanged()
            }
            updateRecyclerViewVisibility(getSelectedTabType())
        }
    }

    private fun updateRecyclerViewVisibility(selectedType: Int) {
        if (selectedType == 0) {
            if (tasksList!!.isNotEmpty()) {
                binding!!.emptyState.isVisible = false
                binding!!.recylerViewTasks.isVisible = true
            } else {
                binding!!.emptyState.isVisible = true
                binding!!.recylerViewTasks.isVisible = false
            }
        } else {
            if (habitsList!!.isNotEmpty()) {
                binding!!.emptyState.isVisible = false
                binding!!.recylerViewTasks.isVisible = true
            } else {
                binding!!.emptyState.isVisible = true
                binding!!.recylerViewTasks.isVisible = false
            }
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
        val filter = IntentFilter("com.cuebit.io.ACTION_TASK_INSERTED")
        requireContext().registerReceiver(taskInsertedReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(taskInsertedReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setCategoryAdapter(categoryList : MutableList<Category>){
        categoryAdapter = CategoryAdapter(categoryList, tasksDAO!!, requireContext(),this)
        binding!!.categoryRecyclerView.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        binding!!.categoryRecyclerView.adapter = categoryAdapter
    }

    companion object {
        private const val TASKS_TAB = 0
        private const val HABITS_TAB = 1
    }

}
