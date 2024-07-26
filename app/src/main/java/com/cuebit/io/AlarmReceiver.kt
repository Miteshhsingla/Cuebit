package com.cuebit.io

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cuebit.Database.TasksDAO
import com.cuebit.io.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var tasksDAO: TasksDAO
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }

        val id = intent.getStringExtra("ID")
        if (id == "-1") {
            return
        }

        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Task Reminder"

        val i = Intent(context, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, "TimeIt")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(taskTitle)
            .setContentText("It's time for your task: $taskTitle")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, do nothing for now
            return
        }
        notificationManager.notify(id!!.toInt(), builder.build())
    }

    fun cancelAlarm(context: Context, Id: String) {
        tasksDAO = TasksDAO.getInstance(context)
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Id.toInt(),
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        CoroutineScope(Dispatchers.Main).launch {
            tasksDAO.deleteAlarm(Id)
            Toast.makeText(context, "Alarm canceled Successfully", Toast.LENGTH_LONG).show()
        }
    }
}
