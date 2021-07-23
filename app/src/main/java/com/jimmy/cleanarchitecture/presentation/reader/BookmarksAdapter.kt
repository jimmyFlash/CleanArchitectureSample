package com.jimmy.cleanarchitecture.presentation.reader

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jimmy.cleanarchitecture.R
import com.jimmy.cleanarchitecture.databinding.ItemBookmarkBinding
import com.jimmy.cleanarchitecture.domain.Bookmark

class BookmarksAdapter(
    private val bookmarks: MutableList<Bookmark> = mutableListOf(),
    private val itemClickListener: (Bookmark) -> Unit // lambda to handle item click
) : RecyclerView.Adapter<BookmarksAdapter.ViewHolder>() {

    lateinit var itemBookmarkBinding: ItemBookmarkBinding

  class ViewHolder(itemBookmarkBinding: ItemBookmarkBinding) :
      RecyclerView.ViewHolder(itemBookmarkBinding.root) {
    val titleTextView: TextView = itemBookmarkBinding.bookmarkNameTextView
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      itemBookmarkBinding = ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context)
         , parent, false)
    return ViewHolder(itemBookmarkBinding)
  }

  override fun getItemCount() = bookmarks.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.titleTextView.text = holder.itemView.resources.getString(
        R.string.page_bookmark_format,
        bookmarks[position].page
    )
    holder.itemView.setOnClickListener { itemClickListener.invoke(bookmarks[position]) }
  }

  fun update(newBookmarks: List<Bookmark>) {
    bookmarks.clear()
    bookmarks.addAll(newBookmarks)

    notifyDataSetChanged()
  }
}