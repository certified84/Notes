package com.certified.notes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.model.Note

class HomeNoteRecyclerAdapter(val id: Int) :
    ListAdapter<Note, HomeNoteRecyclerAdapter.ViewHolder>(
        DIFF_CALLBACK
    ) {

    private lateinit var listener: OnNoteClickedListener

    fun setOnNoteClickedListener(listener: OnNoteClickedListener) {
        this.listener = listener
    }

    interface OnNoteClickedListener {
        fun onNoteClicked(note: Note)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteContent: TextView = itemView.findViewById(R.id.tv_note_content)
        val noteTitle: TextView = itemView.findViewById(R.id.tv_note_title)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener.onNoteClicked(getItem(position))
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Note> =
            object : DiffUtil.ItemCallback<Note>() {
                override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                    return oldItem.title == newItem.title &&
                            oldItem.content == newItem.content &&
                            oldItem.courseCode == newItem.courseCode
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View
        val ID_NOT_SET = 0
        itemView = if (id == ID_NOT_SET)
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_notes_home, parent, false)
        else
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_related_notes, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        holder.noteTitle.text = note.title
        holder.noteContent.text = note.content
    }
}