
package com.jimmy.cleanarchitecture.presentation.library

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.jimmy.cleanarchitecture.R
import com.jimmy.cleanarchitecture.databinding.ItemDocumentBinding
import com.jimmy.cleanarchitecture.domain.Document
import com.jimmy.cleanarchitecture.presentation.StringUtil


class DocumentsAdapter(
    private val documents: MutableList<Document> = mutableListOf(),
    private val glide: RequestManager,
    private val itemClickListener: (Document) -> Unit  // lambda for click listener handler
) : RecyclerView.Adapter<DocumentsAdapter.ViewHolder>() {

  lateinit var itemDocumentBinding: ItemDocumentBinding

  class ViewHolder(itemDocumentBinding: ItemDocumentBinding) :
      RecyclerView.ViewHolder(itemDocumentBinding.root) {
    val previewImageView: ImageView = itemDocumentBinding.ivPreview
    val titleTextView: TextView = itemDocumentBinding.tvTitle
    val sizeTextView: TextView = itemDocumentBinding.tvSize
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
   itemDocumentBinding = ItemDocumentBinding.inflate(LayoutInflater.from(parent.context), parent,
       false)
    return ViewHolder(itemDocumentBinding)
  }

  override fun getItemCount() = documents.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.run {
    glide.load(documents[position].thumbnail)
        .error(glide.load(R.drawable.preview_missing))
        .into(holder.previewImageView)
    holder.previewImageView.setImageResource(R.drawable.preview_missing)
    holder.titleTextView.text = documents[position].name
    holder.sizeTextView.text = StringUtil.readableFileSize(documents[position].size)
    holder.itemView.setOnClickListener { itemClickListener.invoke(documents[position]) }
  }

  fun update(newDocuments: List<Document>) {
    documents.clear()
    documents.addAll(newDocuments)

    notifyDataSetChanged()
  }
}