package com.timeit.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MY_DATABASE"
        private const val DATABASE_VERSION = 3

        // Table and column names for tasks
        const val TABLE_TASKS = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DATETIME = "datetime"
        const val COLUMN_FREQUENCY = "frequency"

        // Table and column names for users
        const val TABLE_USERS = "users"
        const val COLUMN_USER_NAME = "name"

        // Table and column names for categories
        const val TABLE_CATEGORIES = "categories"
        const val COLUMN_CATEGORY_ID = "categoryId"
        const val COLUMN_CATEGORY_NAME = "categoryName"
    }

    private val TABLE_CREATE_TASKS = """
        CREATE TABLE $TABLE_TASKS (
            $COLUMN_ID TEXT PRIMARY KEY, 
            $COLUMN_TITLE TEXT, 
            $COLUMN_DESCRIPTION TEXT, 
            $COLUMN_CATEGORY TEXT, 
            $COLUMN_DATETIME TEXT, 
            $COLUMN_FREQUENCY TEXT
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

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TABLE_CREATE_TASKS)
        db?.execSQL(TABLE_CREATE_USERS)
        db?.execSQL(TABLE_CREATE_CATEGORIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        onCreate(db)
    }

}
