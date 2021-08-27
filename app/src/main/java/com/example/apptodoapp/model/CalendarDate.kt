package com.example.apptodoapp.model

import com.example.apptodoapp.extensions.format
import java.text.SimpleDateFormat
import java.util.*

data class CalendarDate(var data: Date, var isSelected: Boolean = false) {

    val calendarDay: String
        get() = SimpleDateFormat("EE", Locale.getDefault()).format(data)

    val calendarDate: String
        get() {
            val cal = Calendar.getInstance()
            cal.time = data
            return cal[Calendar.DAY_OF_MONTH].toString()
        }

    val completedDate: String
        get() = data.format()
}
