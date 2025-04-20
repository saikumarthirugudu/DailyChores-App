package com.example.dailychoresapp.util

import android.content.Context
import androidx.work.*
import com.example.dailychoresapp.worker.TaskReminderWorker
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleReminder(context: Context, taskId: Int, title: String, dueTimeMillis: Long) {
        val delay = dueTimeMillis - System.currentTimeMillis()
        if (delay <= 0) return  // Skip scheduling past tasks

        val data = Data.Builder()
            .putString("task_title", title)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("task_reminder_$taskId")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "reminder_task_$taskId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelReminder(context: Context, taskId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork("reminder_task_$taskId")
    }
}
