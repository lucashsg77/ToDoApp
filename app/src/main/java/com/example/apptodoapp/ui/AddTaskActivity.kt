package com.example.apptodoapp.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.apptodoapp.R
import com.example.apptodoapp.extensions.text
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.activity_add_task.*
import java.util.*

class AddTaskActivity: AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        if(intent.hasExtra(TASK_EDT)){
            toolbar.title = getString(R.string.editTitle)
            createTaskBtn.text = getString(R.string.saveChanges)
            intent.getBundleExtra(TASK_EDT)?.let{
                titleTi.text = it.getString("title").toString()
                descriptionTi.text = it.getString("description").toString()
                dateTi.text = it.getString("date").toString()
                timeTi.text = it.getString("time").toString()
                intent.putExtra(TASK_EDT, bundleOf("previousDate" to dateTi.text,
                                                         "previousTime" to timeTi.text))
            }
        }
        insertListeners()
    }

    @RequiresApi(Build.VERSION_CODES.N)

    private fun insertListeners() {

        dateTi.editText?.setOnClickListener {
            val c = Calendar.getInstance()
            val date = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                val day = if(dayOfMonth in 1..9) "0${dayOfMonth}" else dayOfMonth
                val month = if(monthOfYear in 1..8) "0${monthOfYear + 1}" else monthOfYear
                dateTi.text = "$day/$month/$year".format()
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            date.datePicker.minDate = Calendar.getInstance(Locale.getDefault()).timeInMillis
            date.show()
        }

        timeTi.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()
            timePicker.addOnPositiveButtonClickListener {
                val minute = if(timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute
                val hour = if(timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour
                timeTi.text = "$hour:$minute"
            }
            timePicker.show(supportFragmentManager, "TIME_PICKER")
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        cancelBtn.setOnClickListener {
            finish()
        }

        createTaskBtn.setOnClickListener {
            if(titleTi.text.isNotBlank() && dateTi.text.isNotBlank() && timeTi.text.isNotBlank()){
                intent.putExtra(TASK_REPLY, bundleOf("title" to titleTi.text,
                                                         "description" to descriptionTi.text,
                                                         "date" to dateTi.text,
                                                         "time" to timeTi.text))
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            else{
                Toast.makeText(this, getString(R.string.toastFillBlanks), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val TASK_EDT = "task_edt"
        const val TASK_REPLY = "task_reply"
    }
}
