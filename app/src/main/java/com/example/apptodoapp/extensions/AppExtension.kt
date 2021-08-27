package com.example.apptodoapp.extensions

import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

private val locale by lazy { Locale.getDefault() }

fun Date.format() : String{
    return SimpleDateFormat("dd/MM/yyyy", locale).format(this)
}

fun toMillis(time: String, date: String): Long{
    val cal: Calendar = Calendar.getInstance()
    val trimmedDate = date.replace("/", "")
    val trimmedTime = time.replace(":", "")
    val day = "${trimmedDate[0]}${trimmedDate[1]}".toInt()
    val month = "${trimmedDate[2]}${trimmedDate[3]}".toInt() - 1
    val year = "${trimmedDate[4]}${trimmedDate[5]}${trimmedDate[6]}${trimmedDate[7]}".toInt()
    val hour = "${trimmedTime[0]}${trimmedTime[1]}".toInt()
    val min = "${trimmedTime[2]}${trimmedTime[3]}".toInt()
    cal.set(year, month, day)
    cal.set(Calendar.HOUR_OF_DAY, hour)
    cal.set(Calendar.MINUTE, min)
    cal.set(Calendar.SECOND, 0)
    return cal.timeInMillis
}

var TextInputLayout.text: String
    get() = editText?.text?.toString() ?: ""
    set(value){
        editText?.setText(value)
    }