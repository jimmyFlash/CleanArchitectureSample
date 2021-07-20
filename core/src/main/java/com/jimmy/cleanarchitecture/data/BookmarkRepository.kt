package com.jimmy.cleanarchitecture.data

import com.jimmy.cleanarchitecture.domain.Bookmark
import com.jimmy.cleanarchitecture.domain.Document

/**
 *  Repository that you’ll use to add, remove and fetch stored bookmarks in the app
 */
class BookmarkRepository (private val dataSource: BookmarkDataSource) {
    suspend fun addBookmark(document: Document, bookmark: Bookmark) =
        dataSource.add(document, bookmark)

    suspend fun getBookmarks(document: Document) = dataSource.read(document)

    suspend fun removeBookmark(document: Document, bookmark: Bookmark) =
        dataSource.remove(document, bookmark)
}