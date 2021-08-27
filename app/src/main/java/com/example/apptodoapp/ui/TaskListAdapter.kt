package com.example.apptodoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apptodoapp.R
import com.example.apptodoapp.model.Task
import kotlinx.android.synthetic.main.item_task.view.*
import java.util.*

class TaskListAdapter: ListAdapter<Task, TaskListAdapter.TaskViewHolder>(DiffCallback()) {
    var listenerEdit: (Task) -> Unit = {}
    var listenerDelete: (Task) -> Unit = {}
    inner class TaskViewHolder(private val binding: View?): RecyclerView.ViewHolder(binding!!){
        fun bind(item: Task){

            if (binding != null) {
                binding.tvTitle.text = item.title
            }
            if (binding != null) {
                "${item.date} ${item.time}".also { binding.tvDate.text = it }
            }
            binding?.ivMore?.setOnClickListener {
                showPopup(item)
            }
        }

        private fun showPopup(item: Task) {
            val ivMore = binding?.ivMore
            val popupMenu = PopupMenu(ivMore?.context, ivMore)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.editAction -> listenerEdit(item)
                    R.id.deleteAction -> listenerDelete(item)
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListAdapter.TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)?.inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: TaskListAdapter.TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}