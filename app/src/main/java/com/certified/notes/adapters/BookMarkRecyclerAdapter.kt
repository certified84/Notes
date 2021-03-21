package com.certified.notes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.certified.notes.R
import com.certified.notes.model.BookMark
import com.like.LikeButton

class BookMarkRecyclerAdapter(
//    private val context: Context,
//    private val viewModel: NotesViewModel
) :
    ListAdapter<BookMark, BookMarkRecyclerAdapter.ViewHolder>(
        DIFF_CALLBACK
    ) {

    private lateinit var listener: OnBookMarkClickedListener

//    init {
//        listener = null
//    }

    fun setOnBookMarkClickedListener(listener: OnBookMarkClickedListener) {
        this.listener = listener
    }

    fun getBookMarkAt(position: Int): BookMark {
        return getItem(position)
    }

    interface OnBookMarkClickedListener {
        fun onBookMarkClick(bookmark: BookMark)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteContent: TextView
        val noteTitle: TextView
        val likeButton: LikeButton
        val ivBookMark: ImageView

        init {
            noteContent = itemView.findViewById(R.id.tv_note_content)
            noteTitle = itemView.findViewById(R.id.tv_note_title)
            likeButton = itemView.findViewById(R.id.likeButton)
            ivBookMark = itemView.findViewById(R.id.iv_bookmark)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onBookMarkClick(getItem(position))
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<BookMark> =
            object : DiffUtil.ItemCallback<BookMark>() {
                override fun areItemsTheSame(oldItem: BookMark, newItem: BookMark): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: BookMark, newItem: BookMark): Boolean {
                    return (oldItem.noteId == newItem.noteId || oldItem.noteContent == newItem.noteContent ||
                            oldItem.courseCode == newItem.courseCode || oldItem.noteTitle == newItem.noteTitle)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_notes, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentBookMark = getItem(position)
        holder.noteContent.text = currentBookMark.noteContent
        holder.noteTitle.text = currentBookMark.noteTitle
        holder.ivBookMark.visibility = View.VISIBLE
//        holder.likeButton.isLiked = true
//        holder.likeButton.setOnLikeListener(object : OnLikeListener {
//            override fun liked(likeButton: LikeButton?) {
//
//            }
//
//            override fun unLiked(likeButton: LikeButton?) {
//                val noteId = currentBookMark.noteId
//                val courseCode = currentBookMark.courseCode
//                val noteTitle = currentBookMark.noteTitle
//                val noteContent = currentBookMark.noteContent
//                val bookMark = BookMark(noteId, courseCode, noteTitle, noteContent)
//                bookMark.id = currentBookMark.id
//                viewModel.deleteBookMark(bookMark)
//
//                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//                val editor = preferences.edit()
//
//                val defValues = HashSet<String>()
//                defValues.add("-1")
//                val noteIds =
//                    HashSet<String>(preferences.getStringSet(PreferenceKeys.NOTE_IDS, defValues))
//                noteIds.remove(noteId.toString())
//
//                editor.putStringSet(PreferenceKeys.NOTE_IDS, noteIds)
//                editor.apply()
//            }
//        })
    }
}