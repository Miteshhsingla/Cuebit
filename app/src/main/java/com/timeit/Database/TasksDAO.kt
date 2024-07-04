package com.timeit.Database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
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
            val cursor = database.query(
                MyDBHelper.TABLE_TASKS,
                null,
                null,
                null,
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

    // Retrieve tasks for a specific date
    @SuppressLint("Range")
    suspend fun getTasksForDate(date: String): List<TaskDataModel> {
        return withContext(Dispatchers.IO) {
            val tasksList = mutableListOf<TaskDataModel>()
            val query = "SELECT * FROM ${MyDBHelper.TABLE_TASKS} WHERE SUBSTR(${MyDBHelper.COLUMN_DATETIME}, 1, 10) = ?"
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
                            it.getString(it.getColumnIndex(MyDBHelper.COLUMN_FREQUENCY))
                        )
                    )
                }
            }
            tasksList
        }
    }
}
