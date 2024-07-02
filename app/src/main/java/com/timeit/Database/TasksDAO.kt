package com.timeit.Database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.timeit.app.DataModels.TaskDataModel
import org.json.JSONArray


class TasksDAO(context: Context?) {
    private val database: SQLiteDatabase
    private val dbHelper: MyDBHelper

    init {
        dbHelper = MyDBHelper(context)
        database = dbHelper.writableDatabase
    }

    fun close() {
        dbHelper.close()
    }

    // Insert a task
    fun insertTask(task: TaskDataModel) {
        val values = ContentValues()
        values.put(MyDBHelper.COLUMN_ID, task.id)
        values.put(MyDBHelper.COLUMN_TITLE, task.title)
        values.put(MyDBHelper.COLUMN_DESCRIPTION, task.description)
        values.put(MyDBHelper.COLUMN_CATEGORY, task.category)
        values.put(MyDBHelper.COLUMN_DATETIME, task.dateAndTime)
        values.put(MyDBHelper.COLUMN_FREQUENCY, task.frequency)
        database.insert(MyDBHelper.TABLE_TASKS, null, values)
    }

    // Retrieve a task
    @SuppressLint("Range")
    fun getTask(id: String): TaskDataModel? {
        val cursor = database.query(
            MyDBHelper.TABLE_TASKS,
            null, MyDBHelper.COLUMN_ID + " = ?", arrayOf<String>(id), null, null, null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndex(MyDBHelper.COLUMN_TITLE))
            val description =
                cursor.getString(cursor.getColumnIndex(MyDBHelper.COLUMN_DESCRIPTION))
            val datetime =
                cursor.getString(cursor.getColumnIndex(MyDBHelper.COLUMN_DATETIME))
            val frequency =
                cursor.getString(cursor.getColumnIndex(MyDBHelper.COLUMN_FREQUENCY))
            val category =
                cursor.getString(cursor.getColumnIndex(MyDBHelper.COLUMN_CATEGORY))

            cursor.close()
            return TaskDataModel(id, title, description, category, datetime, frequency)
        }
        cursor?.close()
        return null
    }

    // Method to save a user's name
    fun saveUserName(name: String) {
        val db = database
        val values = ContentValues().apply {
            put(MyDBHelper.COLUMN_USER_NAME, name)
        }
        db.replace(MyDBHelper.TABLE_USERS, null, values)  // Replaces the existing row if it exists
        db.close()
    }

    // Method to retrieve a user's name
    fun getUserName(): String? {
        val db = database
        val cursor = db.query(MyDBHelper.TABLE_USERS, arrayOf(MyDBHelper.COLUMN_USER_NAME), null, null, null, null, null)
        var userName: String? = null
        if (cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndexOrThrow(MyDBHelper.COLUMN_USER_NAME))
        }
        cursor.close()
        db.close()
        return userName
    }



}

