package com.certified.notes.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.model.BookMark
import com.certified.notes.model.Note
import com.certified.notes.room.NotesViewModel
import com.certified.notes.util.PreferenceKeys
import com.like.LikeButton
import com.like.OnLikeListener
import java.util.*

class NoteRecyclerAdapterKt(private val mContext: Context, private val mOwner: LifecycleOwner,
    private val mViewModel: NotesViewModel
) : ListAdapter<Note, NoteRecyclerAdapterKt.ViewHolder>(
        DIFF_CALLBACK
    ) {

    private val mPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.applicationContext)
    private val mNoteIds: MutableSet<String>
    private val mDefValues: MutableSet<String>
    private var listener: OnNoteClickedListener?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_notes, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val editor = mPreferences.edit()
        val currentNote = getItem(position)
        holder.mNoteContent.text = currentNote!!.content
        holder.mNoteTitle.text = currentNote.title
        holder.mLikeButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                val noteId = currentNote.id
                val courseCode = currentNote.courseCode
                val noteTitle = currentNote.title
                val noteContent = currentNote.content

                val bookMark = BookMark(noteId, courseCode, noteTitle, noteContent!!)
                mViewModel.insertBookMark(bookMark)

                mNoteIds.add(noteId.toString())
                editor.putStringSet(PreferenceKeys.NOTE_IDS, mNoteIds)
                editor.apply()
            }

            override fun unLiked(likeButton: LikeButton) {
                mViewModel.getBookMarkAt(currentNote.id).observe(mOwner, Observer { bookMark :BookMark? ->
                        if (bookMark != null) {
                            mViewModel.deleteBookMark(bookMark)
                        }
                        mNoteIds.remove((currentNote.id).toString())
                        editor.putStringSet(PreferenceKeys.NOTE_IDS, mNoteIds)
                        editor.apply()
                    }
                )
            }
        })
        holder.checkIfBookMarked(currentNote.id, holder.mLikeButton)
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
        val mNoteContent: TextView
        val mNoteTitle: TextView
        val mLikeButton: LikeButton

        fun checkIfBookMarked(noteId: Int, likeButton: LikeButton) {
            val defValues: MutableSet<String> = HashSet()
            defValues.add("-1")
            val noteIds: Set<String> = HashSet(mPreferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues))
            if (noteId.toString() in noteIds) {
                likeButton.isLiked = true
            }
        }

        init {
            mNoteContent = itemView.findViewById(R.id.tv_note_content)
            mNoteTitle = itemView.findViewById(R.id.tv_note_title)
            mLikeButton = itemView.findViewById(R.id.likeButton)
            itemView.setOnClickListener {_: View? ->
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
        mDefValues = HashSet()
        mDefValues.add("-1")
        mNoteIds = HashSet(mPreferences.getStringSet(PreferenceKeys.NOTE_IDS, mDefValues))
        listener = null
    }
}