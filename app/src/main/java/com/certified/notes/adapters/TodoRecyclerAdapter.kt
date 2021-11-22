package com.certified.notes.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.model.Todo
import com.certified.notes.ui.home.HomeViewModel
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TodoRecyclerAdapter(val context: Context, private val viewModel: HomeViewModel) :
    ListAdapter<Todo, TodoRecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

    private lateinit var listener: OnTodoClickedListener

    fun setOnTodoClickedListener(listener: OnTodoClickedListener) {
        this.listener = listener
    }

    interface OnTodoClickedListener {
        fun onTodoClicked(todo: Todo)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todo: TextView = itemView.findViewById(R.id.tv_todo)
        val checkBok: MaterialCheckBox = itemView.findViewById(R.id.checkbox_todo)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener.onTodoClicked(getItem(position))
            }
        }
    }

    private fun strikeThrough(todo: TextView, strikeThrough: Boolean) {
        if (strikeThrough)
            todo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        else
            todo.paintFlags = 0
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Todo> =
            object : DiffUtil.ItemCallback<Todo>() {
                override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                    return oldItem.todo == newItem.todo &&
                            oldItem.isDone == newItem.isDone
                }
            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoRecyclerAdapter.ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.list_item_todos, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TodoRecyclerAdapter.ViewHolder, position: Int) {
        val todo = getItem(position)
        holder.todo.text = todo.todo
        holder.checkBok.isChecked = todo.isDone
        if (holder.checkBok.isChecked)
            strikeThrough(holder.todo, holder.checkBok.isChecked)

        holder.checkBok.setOnClickListener {
            val todoContent = holder.todo.text.toString()
            if (holder.checkBok.isChecked) {
                val builder = MaterialAlertDialogBuilder(context)
                builder.setIcon(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_baseline_delete_24
                    )
                )
                builder.setTitle(context.getString(R.string.delete_completed_todo))
                builder.setMessage(context.getString(R.string.delete_completed_todo_message))
                builder.setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
                    viewModel.deleteTodo(getItem(position))
                    dialog.cancel()
                }
                builder.setNegativeButton(context.getString(R.string.no)) { dialog, _ ->
                    val todo1 = Todo(todoContent, true)
                    todo1.id = todo.id
                    viewModel.updateTodo(todo1)
                    dialog.cancel()
                }
                builder.setOnDismissListener { holder.checkBok.isChecked = false }
                val dialog = builder.create()
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                }
                dialog.show()
            }
        }
    }
}