package com.timeit.Database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.annotation.RequiresApi
import com.timeit.app.DataModels.Category
import com.timeit.app.DataModels.HabitDataModel
import com.timeit.app.DataModels.TaskDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksDAO(context: Context?) {
    private val database: SQLiteDatabase
    private val dbHelper: MyDBHelper
    private val contentValues: ContentValues = ContentValues()

    init {
        dbHelper = MyDBHelper(context)
        database = dbHelper.writableDatabase
    }

    fun close() {
        dbHelper.close()
    }

    // Insert a task
    suspend fun insertTask(task: TaskDataModel) {
        withContext(Dispatchers.IO) {
            contentValues.clear()
            contentValues.apply {
                put(MyDBHelper.COLUMN_ID, task.id)
                put(MyDBHelper.COLUMN_TITLE, task.title)
                put(MyDBHelper.COLUMN_DESCRIPTION, task.description)
                put(MyDBHelper.COLUMN_CATEGORY, task.category)
                put(MyDBHelper.COLUMN_DATETIME, task.dateAndTime)
                put(MyDBHelper.COLUMN_FREQUENCY, task.frequency)
                put(MyDBHelper.COLUMN_TASK_STATUS, task.markAsDone)
            }
            database.insert(MyDBHelper.TABLE_TASKS, null, contentValues)
        }
    }

    // Retrieve a task
    @SuppressLint("Range")
    suspend fun getTask(id: String): TaskDataModel? {
        return withContext(Dispatchers.IO) {
            val cursor = database.query(
                MyDBHelper.TABLE_TASKS,
                null,
                "${MyDBHelper.COLUMN_ID} = ?",
                arrayOf(id),
                null,
                null,
                null
            )
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    TaskDataModel(
                        id,
                        it.getString(it.getColumnIndex(MyDBHelper.COLUMN_TITLE)),
                        it.getString(it.getColumnIndex(MyDBHelper.COLUMN_DESCRIPTION)),
                        it.getString(it.getColumnIndex(MyDBHelper.COLUMN_CATEGORY)),
                        it.getString(it.getColumnIndex(MyDBHelper.COLUMN_DATETIME)),
                        it.getString(it.getColumnIndex(MyDBHelper.COLUMN_FREQUENCY))
                    )
                } else null
            }
        }
    }

    // Retrieve all tasks
    @SuppressLint("Range")
    suspend fun getAllTasks(): List<TaskDataModel> {
        return withContext(Dispatchers.IO) {
            val tasksList = mutableListOf<TaskDataModel>()
            val columns = arrayOf(
                MyDBHelper.COLUMN_ID,
                MyDBHelper.COLUMN_TITLE,
                MyDBHelper.COLUMN_DESCRIPTION,
                MyDBHelper.COLUMN_CATEGORY,
                MyDBHelper.COLUMN_DATETIME,
                MyDBHelper.COLUMN_FREQUENCY,
                MyDBHelper.COLUMN_TASK_STATUS
            )
            // Specify the selection criteria
            val selection = "${MyDBHelper.COLUMN_TASK_STATUS} = ?"
            // Assuming '0' indicates the task is not done
            val selectionArgs = arrayOf("0")
            val cursor = database.query(
                MyDBHelper.TABLE_TASKS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            cursor.use {
                while (it.moveToNext()) {
                    tasksList.add(
                        TaskDataModel(
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_ID)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_TITLE)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_DESCRIPTION)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_CATEGORY)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_DATETIME)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_FREQUENCY)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_TASK_STATUS))

                        )
                    )
                }
            }
            tasksList
        }
    }

    // Retrieve tasks for a specific date
    @SuppressLint("Range")
    suspend fun getTasksForDate(date: String): List<TaskDataModel> {
        return withContext(Dispatchers.IO) {
            val tasksList = mutableListOf<TaskDataModel>()
            val query =
                "SELECT * FROM ${MyDBHelper.TABLE_TASKS} WHERE SUBSTR(${MyDBHelper.COLUMN_DATETIME}, 1, 10) = ? AND ${MyDBHelper.COLUMN_TASK_STATUS} = '0'"
            val cursor = database.rawQuery(query, arrayOf(date))
            cursor.use {
                while (it.moveToNext()) {
                    tasksList.add(
                        TaskDataModel(
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_ID)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_TITLE)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_DESCRIPTION)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_CATEGORY)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_DATETIME)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_FREQUENCY)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_TASK_STATUS))
                        )
                    )
                }
            }
            tasksList
        }
    }

    // Retrieve tasks for a specific category
    @SuppressLint("Range")
    suspend fun getTasksByCategory(category: String): List<TaskDataModel> {
        return withContext(Dispatchers.IO) {
            val tasksList = mutableListOf<TaskDataModel>()
            val cursor = database.query(
                MyDBHelper.TABLE_TASKS,
                null,
                "${MyDBHelper.COLUMN_CATEGORY} = ? AND ${MyDBHelper.COLUMN_TASK_STATUS} = ?",
                arrayOf(category, "0"),
                null,
                null,
                null
            )
            cursor.use {
                while (it.moveToNext()) {
                    tasksList.add(
                        TaskDataModel(
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_ID)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_TITLE)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_DESCRIPTION)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_CATEGORY)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_DATETIME)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_FREQUENCY))
                        )
                    )
                }
            }
            tasksList
        }
    }

    suspend fun markTaskAsDone(id: String) {
        withContext(Dispatchers.IO) {
            val contentValues = ContentValues().apply {
                put(MyDBHelper.COLUMN_TASK_STATUS, "1")
            }
            database.update(MyDBHelper.TABLE_TASKS, contentValues, "${MyDBHelper.COLUMN_ID} = ?", arrayOf(id))
        }
    }

    suspend fun deleteTask(id: String) {
        withContext(Dispatchers.IO) {
            database.delete(MyDBHelper.TABLE_TASKS, "${MyDBHelper.COLUMN_ID} = ?", arrayOf(id))
        }
    }

    suspend fun addCategory(categoryName: String, categoryId: String) {
        withContext(Dispatchers.IO) {

            val db = database
            val values = ContentValues().apply {
                put(MyDBHelper.COLUMN_CATEGORY_ID, categoryId)
                put(MyDBHelper.COLUMN_CATEGORY_NAME, categoryName)
            }
            db.insert(MyDBHelper.TABLE_CATEGORIES, null, values)
        }
    }

    suspend fun fetchAllCategories(): MutableList<Category> {
        return withContext(Dispatchers.IO) {

            val categories = mutableListOf<Category>()
            val db = database
            val cursor: Cursor = db.rawQuery("SELECT * FROM ${MyDBHelper.TABLE_CATEGORIES}", null)
            cursor.use {
                if (it.moveToFirst()) {
                    do {
                        val categoryId =
                            it.getString(it.getColumnIndexOrThrow(MyDBHelper.COLUMN_CATEGORY_ID))
                        val categoryName =
                            it.getString(it.getColumnIndexOrThrow(MyDBHelper.COLUMN_CATEGORY_NAME))
                        categories.add(Category(categoryId, categoryName))
                    } while (it.moveToNext())
                }
            }
            categories
        }
    }

    suspend fun isCategoryExist(categoryName: String): Boolean {
        return withContext(Dispatchers.IO) {
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                MyDBHelper.TABLE_CATEGORIES,
                arrayOf(MyDBHelper.COLUMN_CATEGORY_NAME),
                "${MyDBHelper.COLUMN_CATEGORY_NAME} = ?",
                arrayOf(categoryName),
                null,
                null,
                null
            )
            val exists = cursor.count > 0
            cursor.close()
            exists
        }
    }

    suspend fun deleteCategory(categoryId: String) {
        withContext(Dispatchers.IO) {

            val db = database
             db.delete(
                MyDBHelper.TABLE_CATEGORIES,
                "${MyDBHelper.COLUMN_CATEGORY_ID} = ?",
                arrayOf(categoryId)
            )
        }
    }

    suspend fun insertHabit(habit: HabitDataModel) {
        withContext(Dispatchers.IO) {
            contentValues.clear()
            contentValues.apply {
                put(MyDBHelper.COLUMN_HABIT_ID, habit.id)
                put(MyDBHelper.COLUMN_HABIT_TITLE, habit.habitName)
                put(MyDBHelper.COLUMN_HABIT_DESCRIPTION, habit.description)
                put(MyDBHelper.COLUMN_HABIT_GOAL, habit.goal)
                put(MyDBHelper.COLUMN_HABIT_DATE, habit.startDate)
                put(MyDBHelper.COLUMN_HABIT_REMINDER, habit.reminder)
                put(MyDBHelper.COLUMN_HABIT_FREQUENCY, habit.frequency)
                put(MyDBHelper.COLUMN_HABIT_IMAGE, habit.image)
                put(MyDBHelper.COLUMN_HABIT_STATUS, habit.isCompleted)
            }
            database.insert(MyDBHelper.TABLE_HABITS, null, contentValues)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    suspend fun getHabitsForDate(date: String): List<HabitDataModel> {
        return withContext(Dispatchers.IO) {
            val query =
                "SELECT * FROM ${MyDBHelper.TABLE_HABITS} WHERE ${MyDBHelper.COLUMN_HABIT_DATE} = ? AND ${MyDBHelper.COLUMN_HABIT_STATUS} = '0'"

            val habitsList = mutableListOf<HabitDataModel>()
            val cursor = database.rawQuery(query, arrayOf(date))
            cursor.use {
                while (it.moveToNext()) {
                    habitsList.add(
                        HabitDataModel(
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_HABIT_ID)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_HABIT_TITLE)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_HABIT_DESCRIPTION)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_HABIT_FREQUENCY)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_HABIT_STATUS)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_HABIT_REMINDER)),
                            it.getInt(it.getColumnIndex(MyDBHelper.COLUMN_HABIT_IMAGE)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_HABIT_GOAL)),
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_HABIT_DATE))

                        )
                    )
                }
            }
            habitsList
        }
    }

    suspend fun markHabitAsDone(id: String) {
        withContext(Dispatchers.IO) {
            val contentValues = ContentValues().apply {
                put(MyDBHelper.COLUMN_HABIT_STATUS, "1")
            }
            database.update(MyDBHelper.TABLE_HABITS, contentValues, "${MyDBHelper.COLUMN_HABIT_ID} = ?", arrayOf(id))
        }
    }

}
