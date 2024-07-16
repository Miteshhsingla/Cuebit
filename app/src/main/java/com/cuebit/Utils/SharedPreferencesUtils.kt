package com.cuebit.Utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtils {

    private const val PREFS_NAME = "your_prefs_name"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun putString(context: Context, key: String, value: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(context: Context, key: String, defaultValue: String = ""): String {
        return getSharedPreferences(context).getString(key, defaultValue) ?: defaultValue
    }

    fun putInt(context: Context, key: String, value: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(context: Context, key: String, defaultValue: Int = 0): Int {
        return getSharedPreferences(context).getInt(key, defaultValue)
    }

    fun putBoolean(context: Context, key: String, value: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        return getSharedPreferences(context).getBoolean(key, defaultValue)
    }

    fun putFloat(context: Context, key: String, value: Float) {
        val editor = getSharedPreferences(context).edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun getFloat(context: Context, key: String, defaultValue: Float = 0f): Float {
        return getSharedPreferences(context).getFloat(key, defaultValue)
    }

    fun putLong(context: Context, key: String, value: Long) {
        val editor = getSharedPreferences(context).edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(context: Context, key: String, defaultValue: Long = 0L): Long {
        return getSharedPreferences(context).getLong(key, defaultValue)
    }

    fun remove(context: Context, key: String) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(key)
        editor.apply()
    }

    fun clear(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.clear()
        editor.apply()
    }

    fun putDrawableResId(context: Context, key: String, resId: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(key, resId)
        editor.apply()
    }

    fun getDrawableResId(context: Context, key: String, defaultValue: Int = 0): Int {
        return getSharedPreferences(context).getInt(key, defaultValue)
    }
}
