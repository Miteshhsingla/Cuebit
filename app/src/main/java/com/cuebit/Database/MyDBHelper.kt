package com.cuebit.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MY_DATABASE"
        private const val DATABASE_VERSION = 6

        // Table and column names for tasks
        const val TABLE_TASKS = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DATETIME = "datetime"
        const val COLUMN_FREQUENCY = "frequency"
        const val COLUMN_TASK_STATUS = "isDone"

        // Table and column names for users
        const val TABLE_USERS = "users"
        const val COLUMN_USER_NAME = "name"

        // Table and column names for categories
        const val TABLE_CATEGORIES = "categories"
        const val COLUMN_CATEGORY_ID = "categoryId"
        const val COLUMN_CATEGORY_NAME = "categoryName"

        // Table and column names for Habits
        const val TABLE_HABITS = "habits"
        const val COLUMN_HABIT_ID = "id"
        const val COLUMN_HABIT_TITLE = "title"
        const val COLUMN_HABIT_DESCRIPTION = "description"
        const val COLUMN_HABIT_GOAL = "goal"
        const val COLUMN_HABIT_DATE = "startDate"
        const val COLUMN_HABIT_REMINDER = "reminder"
        const val COLUMN_HABIT_FREQUENCY = "frequency"
        const val COLUMN_HABIT_IMAGE = "image"
        const val COLUMN_HABIT_STATUS = "isDone"

        // Table and column names for Alarms
        const val TABLE_ALARMS = "alarms"
        const val COLUMN_ALARM_ID = "alarmId"
        const val COLUMN_TASK_HABIT_ID = "id"
        const val COLUMN_ALARM_DATE = "date"
        const val COLUMN_ALARM_TIME = "time"
    }

    private val TABLE_CREATE_TASKS = """
        CREATE TABLE $TABLE_TASKS (
            $COLUMN_ID TEXT PRIMARY KEY, 
            $COLUMN_TITLE TEXT, 
            $COLUMN_DESCRIPTION TEXT, 
            $COLUMN_CATEGORY TEXT, 
            $COLUMN_DATETIME TEXT, 
            $COLUMN_FREQUENCY TEXT,
            $COLUMN_TASK_STATUS TEXT

        );
    """.trimIndent()

    private val TABLE_CREATE_USERS = """
        CREATE TABLE $TABLE_USERS (
            $COLUMN_USER_NAME TEXT PRIMARY KEY
        );
    """.trimIndent()

    private val TABLE_CREATE_CATEGORIES = """
        CREATE TABLE $TABLE_CATEGORIES (
            $COLUMN_CATEGORY_ID TEXT PRIMARY KEY,
            $COLUMN_CATEGORY_NAME TEXT
        );
    """.trimIndent()

    private val TABLE_CREATE_HABITS = """
        CREATE TABLE $TABLE_HABITS (
            $COLUMN_HABIT_ID TEXT PRIMARY KEY, 
            $COLUMN_HABIT_TITLE TEXT, 
            $COLUMN_HABIT_DESCRIPTION TEXT, 
            $COLUMN_HABIT_GOAL TEXT,
            $COLUMN_HABIT_DATE TEXT,
            $COLUMN_HABIT_REMINDER TEXT,
            $COLUMN_HABIT_FREQUENCY TEXT,
            $COLUMN_HABIT_IMAGE INT,
            $COLUMN_HABIT_STATUS TEXT

        );
    """.trimIndent()

    private val TABLE_CREATE_ALARMS = """
        CREATE TABLE $TABLE_ALARMS (
            $COLUMN_ALARM_ID TEXT PRIMARY KEY, 
            $COLUMN_TASK_HABIT_ID TEXT,
            $COLUMN_ALARM_DATE TEXT,
            $COLUMN_ALARM_TIME TEXT
        );
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TABLE_CREATE_TASKS)
        db?.execSQL(TABLE_CREATE_USERS)
        db?.execSQL(TABLE_CREATE_CATEGORIES)
        db?.execSQL(TABLE_CREATE_HABITS)
        db?.execSQL(TABLE_CREATE_ALARMS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3){
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
            onCreate(db)
        }
        if (oldVersion < 4) {
            db?.execSQL("ALTER TABLE $TABLE_TASKS ADD COLUMN $COLUMN_TASK_STATUS TEXT")
        }
        if (oldVersion < 5) {
           db?.execSQL(TABLE_CREATE_HABITS)
        }
        if(oldVersion < 6) {
            db?.execSQL(TABLE_CREATE_ALARMS)
        }
//        onCreate(db)
    }

}
