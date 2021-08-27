package com.example.apptodoapp.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.apptodoapp.model.Task

class DiffCallback: DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem.id == newItem.id
}