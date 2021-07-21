package com.jimmy.cleanarchitecture.framework

import android.content.Context
import com.jimmy.cleanarchitecture.data.BookmarkDataSource
import com.jimmy.cleanarchitecture.domain.Bookmark
import com.jimmy.cleanarchitecture.domain.Document
import com.jimmy.cleanarchitecture.framework.db.BookmarkEntity
import com.jimmy.cleanarchitecture.framework.db.MajesticReaderDatabase

class RoomBookmarkDataSource (context: Context) : BookmarkDataSource {

    // Use MajesticReaderDatabase to get an instance of BookmarkDao and store it in local field
    private val bookmarkDao = MajesticReaderDatabase.getInstance(context).bookmarkDao()

    // Call add, read and remove methods from Room implementation
    override suspend fun add(document: Document, bookmark: Bookmark) =
        bookmarkDao.addBookmark(
            BookmarkEntity(
                documentUri = document.url,
                page = bookmark.page
            )
        )

    override suspend fun read(document: Document): List<Bookmark> =
        bookmarkDao.getBookmarks(document.url).map { Bookmark(it.id, it.page) }

    override suspend fun remove(document: Document, bookmark: Bookmark) =
        bookmarkDao.removeBookmark(
            BookmarkEntity(id = bookmark.id, documentUri = document.url, page = bookmark.page)
        )
}