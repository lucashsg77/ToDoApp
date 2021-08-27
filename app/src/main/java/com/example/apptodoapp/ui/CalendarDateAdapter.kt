package com.example.apptodoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.apptodoapp.R
import com.example.apptodoapp.model.CalendarDate

class CalendarDateAdapter(private val listener: (calendarDateModel: CalendarDate, position: Int) -> Unit) :
    RecyclerView.Adapter<CalendarDateAdapter.CalendarDateViewHolder>() {
    private val list = ArrayList<CalendarDate>()

    inner class CalendarDateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(calendarDateModel: CalendarDate) {
            val calendarDay = itemView.findViewById<TextView>(R.id.tvCalendarDay)
            val calendarDate = itemView.findViewById<TextView>(R.id.tvCalendarDate)
            val cardView = itemView.findViewById<CardView>(R.id.card_calendar)

            if (calendarDateModel.isSelected) {
                calendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                calendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
            } else {
                calendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                calendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
            }

            calendarDay.text = calendarDateModel.calendarDay
            calendarDate.text = calendarDateModel.calendarDate
            cardView.setOnClickListener {
                listener.invoke(calendarDateModel, adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDateViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
        return CalendarDateViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarDateViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(calendarList: ArrayList<CalendarDate>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }
}