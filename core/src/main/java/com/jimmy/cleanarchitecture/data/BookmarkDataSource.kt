package com.jimmy.cleanarchitecture.data

import com.jimmy.cleanarchitecture.domain.Bookmark
import com.jimmy.cleanarchitecture.domain.Document

/**
 * This data source will take care of adding, querying and removing
 * bookmarks per documents from the database
 */
interface BookmarkDataSource {

    suspend fun add(document: Document, bookmark: Bookmark)

    suspend fun read(document: Document): List<Bookmark>

    suspend fun remove(document: Document, bookmark: Bookmark)
}