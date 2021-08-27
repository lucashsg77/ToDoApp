package com.example.apptodoapp.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.apptodoapp.R
import com.example.apptodoapp.datasource.TaskApplication
import com.example.apptodoapp.extensions.toMillis
import com.example.apptodoapp.model.CalendarDate
import com.example.apptodoapp.model.Task
import com.example.apptodoapp.notifications.receiver.NotificationReceiver
import com.example.apptodoapp.viewmodel.TaskViewModel
import com.example.apptodoapp.viewmodel.TaskViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.emptyState
import kotlinx.android.synthetic.main.empty_space.*
import kotlinx.android.synthetic.main.row_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var calendarAdapter: CalendarDateAdapter
    private lateinit var selectedDate: String
    private val taskAdapter: TaskListAdapter = TaskListAdapter()
    private val dateFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance(Locale.getDefault())
    private val currentDate = Calendar.getInstance(Locale.getDefault())
    private val dates = ArrayList<Date>()
    private val datesList = ArrayList<CalendarDate>()
    private val taskViewModel: TaskViewModel by viewModels{
        TaskViewModelFactory((application as TaskApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notificationChannel()
        setUpAdapters()
        insertListeners()
        setUpCalendar()
    }

    override fun onResume() {
        super.onResume()
        setUpTasksByDate()
        setUpCalendar()
    }

    private fun setUpCalendar(){
        val calendarList = ArrayList<CalendarDate>()
        rowCalendar.tvDateMonth.text = dateFormatter.format(calendar.time)
        val monthCalendar = calendar.clone() as Calendar
        val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)
            if(monthCalendar == currentDate) {
                val date = CalendarDate(monthCalendar.time)
                date.isSelected = true
                calendarList.add(date)
                rowCalendar.calendarRV.layoutManager?.smoothScrollToPosition(rowCalendar.calendarRV, null, calendarList.indexOf(date) + 2)
            }
            else calendarList.add(CalendarDate(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        datesList.clear()
        datesList.addAll(calendarList)
        calendarAdapter.setData(calendarList)
    }

    private fun setUpAdapters(){
        taskListRV.adapter = taskAdapter
        calendarAdapter = CalendarDateAdapter{
                _: CalendarDate, position: Int -> datesList.forEachIndexed{ index , calendarModel ->
                    calendarModel.isSelected = index == position
            }
            calendarAdapter.setData(datesList)
            setUpTasksByDate()
        }
        rowCalendar.calendarRV.adapter = calendarAdapter
    }

    private fun setUpTasksByDate(){
        taskViewModel.tasks.observe(this){ taskList ->
            selectedDate = datesList.find { it.isSelected }?.completedDate.toString()
            val list = taskViewModel.tasks.value?.filter { it.date == selectedDate}
            emptyStateVisibility(list)
            taskList.let{ taskAdapter.submitList(list)}
        }
    }

    private fun emptyStateVisibility(list: List<Task>?) {
        if (list.isNullOrEmpty()) {
            taskListRV.visibility = View.INVISIBLE
            emptyState.visibility = View.VISIBLE
        } else {
            taskListRV.visibility = View.VISIBLE
            emptyState.visibility = View.INVISIBLE
        }
    }

    private fun insertListeners() {
        insertBtn.setOnClickListener {
            startActivityForResult(Intent(this@MainActivity, AddTaskActivity::class.java), CREATE_NEW_TASK)
        }
        taskAdapter.listenerEdit = {
            startActivityForResult(Intent(this, AddTaskActivity::class.java)
                .putExtra(AddTaskActivity.TASK_EDT, bundleOf("title" to it.title,
                                                                    "description" to it.description,
                                                                    "date" to it.date,
                                                                    "time" to it.time)), CREATE_NEW_TASK)
        }
        taskAdapter.listenerDelete = {
            cancelNotification(this, it)
            taskViewModel.deleteTask(it)
        }
        rowCalendar.ivCalendarNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            setUpCalendar()
        }
        rowCalendar.ivCalendarPrevious.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            if (calendar == currentDate) {
                setUpCalendar()
            }
            else {
                setUpCalendar()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CREATE_NEW_TASK && resultCode == Activity.RESULT_OK){
            data?.getBundleExtra(AddTaskActivity.TASK_REPLY)?.let{ reply ->
                val title = reply.getString("title").toString()
                val description = reply.getString("description").toString()
                val date = reply.getString("date").toString()
                val time = reply.getString("time").toString()
                when {
                    data.hasExtra(AddTaskActivity.TASK_EDT) -> {
                        val prevBundle = data.getBundleExtra(AddTaskActivity.TASK_EDT)
                        val prevDate = prevBundle?.getString("previousDate").toString()
                        val prevTime = prevBundle?.getString("previousTime").toString()
                        val id = taskViewModel.tasks.value?.let { task ->
                            task.find{it.date == prevDate && it.time == prevTime}?.id
                        }
                        val task = id?.let { Task(title, description, date, time, it) }
                        task?.let {
                            taskViewModel.updateTask(it)
                            Toast.makeText(
                                this ,
                                getString(R.string.taskUpdated) ,
                                Toast.LENGTH_SHORT
                            ).show()
                            if(task.date != prevDate || task.time != prevTime){
                                cancelNotification(this, task)
                                scheduleNotification(this, task)
                            }
                        }
                    }
                    !data.hasExtra(AddTaskActivity.TASK_EDT) -> {
                        val task = Task(title, description, date, time, (taskViewModel.tasks.value?.size ?: 1))
                        if(taskViewModel.tasks.value?.find { it.date == date && it.time == time} == null) {
                            taskViewModel.insertTask(task)
                            if(emptyState.visibility == View.VISIBLE) emptyState.visibility = View.INVISIBLE
                            else {}
                            Toast.makeText(
                                this ,
                                getString(R.string.taskCreated) ,
                                Toast.LENGTH_SHORT
                            ).show()
                            scheduleNotification(this, task)
                        }
                        else Toast.makeText(
                            this ,
                            getString(R.string.toastSchedule) ,
                            Toast.LENGTH_SHORT
                            ).show()
                    }
                    else ->{
                        throw Exception(getString(R.string.exceptionError))
                    }
                }
            }
        }
        else{
            Toast.makeText(
                applicationContext,
                getString(R.string.taskNotSaved),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun notificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("notificationChannel", "ReminderChannel", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Channel for the todo app"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun scheduleNotification(context: Context , task: Task){
        val notificationIntent = Intent(context, NotificationReceiver::class.java)
        notificationIntent.putExtra("task_title", task.title)
        notificationIntent.putExtra("task_descr", "${task.date} ${task.time}\n${ task.description }")
        notificationIntent.putExtra("notification_id", task.id)
        val pendingIntent = PendingIntent.getBroadcast(context, task.id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, toMillis(task.time, task.date), pendingIntent)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelNotification(context: Context , task: Task){
        val notificationIntent = Intent(context, NotificationReceiver::class.java)
        notificationIntent.putExtra("task_title", task.title)
        notificationIntent.putExtra("task_descr", "${task.date} ${task.time} \n ${ task.description }")
        notificationIntent.putExtra("notification_id", task.id)
        val pendingIntent = PendingIntent.getBroadcast(context, task.id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pendingIntent)
    }

    companion object{
        private const val CREATE_NEW_TASK = 1000
    }
}