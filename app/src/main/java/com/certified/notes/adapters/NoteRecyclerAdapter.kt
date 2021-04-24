package com.certified.notes.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.model.Note
import com.certified.notes.util.PreferenceKeys
import com.like.LikeButton

class NoteRecyclerAdapter(context: Context) :
    ListAdapter<Note, NoteRecyclerAdapter.ViewHolder>(
        DIFF_CALLBACK
    ) {

    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private val noteIds: MutableSet<String>
    private val defValues: MutableSet<String>
    private var listener: OnNoteClickedListener?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_notes, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val editor = preferences.edit()
        val currentNote = getItem(position)
        holder.mNoteContent.text = currentNote!!.content
        holder.mNoteTitle.text = currentNote.title
//        holder.mLikeButton.setOnLikeListener(object : OnLikeListener {
//            override fun liked(likeButton: LikeButton) {
//                val noteId = currentNote.id
//                val courseCode = currentNote.courseCode
//                val noteTitle = currentNote.title
//                val noteContent = currentNote.content
//
//                val bookMark = BookMark(noteId, courseCode, noteTitle, noteContent!!)
//                mViewModel.insertBookMark(bookMark)
//
//                noteIds.add(noteId.toString())
//                editor.putStringSet(PreferenceKeys.NOTE_IDS, noteIds)
//                editor.apply()
//            }
//
//            override fun unLiked(likeButton: LikeButton) {
//                mViewModel.getBookMarkAt(currentNote.id).observe(mOwner, Observer { bookMark :BookMark? ->
//                        if (bookMark != null) {
//                            mViewModel.deleteBookMark(bookMark)
//                        }
//                        noteIds.remove((currentNote.id).toString())
//                        editor.putStringSet(PreferenceKeys.NOTE_IDS, noteIds)
//                        editor.apply()
//                    }
//                )
//            }
//        })
        holder.checkIfBookMarked(currentNote.id, holder.ivBookMark)
    }

    fun getNoteAt(position: Int): Note? {
        return getItem(position)
    }

    fun setOnNoteClickedListener(listener: OnNoteClickedListener?) {
        this.listener = listener
    }

    interface OnNoteClickedListener {
        fun onNoteClick(note: Note?)
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val mNoteContent: TextView = itemView.findViewById(R.id.tv_note_content)
        val mNoteTitle: TextView = itemView.findViewById(R.id.tv_note_title)
        private val mLikeButton: LikeButton = itemView.findViewById(R.id.likeButton)
        val ivBookMark: ImageView = itemView.findViewById(R.id.iv_bookmark)

        fun checkIfBookMarked(noteId: Int, imageView: ImageView) {
            val defValues = mutableSetOf<String>()
            defValues.add("-1")
            val noteIds = mutableSetOf<String>()
            preferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues)
                ?.let { noteIds.addAll(it) }
            if (noteId.toString() in noteIds)
                imageView.visibility = View.VISIBLE
            else
                imageView.visibility = View.INVISIBLE
        }

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener!!.onNoteClick(getItem(position))
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Note> =
            object : DiffUtil.ItemCallback<Note>() {
                override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                    return (oldItem.title == newItem.title || oldItem.content == newItem.content) &&
                            oldItem.courseCode == newItem.courseCode
                }
            }
    }

    init {
        defValues = mutableSetOf()
        defValues.add("-1")
        noteIds = HashSet(preferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues))
//        preferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues)?.let { noteIds.addAll(it) }
        listener = null
    }
}