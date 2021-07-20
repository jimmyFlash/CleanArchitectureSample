package com.jimmy.cleanarchitecture.interactors

import com.jimmy.cleanarchitecture.data.BookmarkRepository
import com.jimmy.cleanarchitecture.domain.Document

/**
 * Getting all bookmarks for currently open documents use case
 * Each use case class has only one function that invokes the use case.
 * For convenience, you’re overloading the invoke operator.
 * This enables you to simplify the function call on GetBookmarks instance to getBookmarks()
 * instead of getBookmarks.invoke().
 */
class GetBookmarks (private val bookmarkRepository: BookmarkRepository) {

    suspend operator fun invoke(document: Document) =
        bookmarkRepository.getBookmarks(document)
}