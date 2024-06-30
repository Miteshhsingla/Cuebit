package com.timeit.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MY_DATABASE"
        private const val DATABASE_VERSION = 1

        // Table and column names
        val TABLE_TASKS: String? = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DATETIME = "datetime"
        val COLUMN_FREQUENCY: String? = "frequency"
    }

    private val TABLE_CREATE = "CREATE TABLE $TABLE_TASKS" + " (" +
            COLUMN_ID + " TEXT PRIMARY KEY, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_CATEGORY + " TEXT, " +
            COLUMN_DATETIME + " TEXT, " +
            COLUMN_FREQUENCY + " TEXT" +
            ");"


    override fun onCreate( db: SQLiteDatabase?) {
        db?.execSQL(TABLE_CREATE);
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS");
        onCreate(db);
    }
}