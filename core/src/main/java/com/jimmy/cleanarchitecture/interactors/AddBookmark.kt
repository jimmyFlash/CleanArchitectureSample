package com.jimmy.cleanarchitecture.interactors

import com.jimmy.cleanarchitecture.data.BookmarkRepository
import com.jimmy.cleanarchitecture.domain.Bookmark
import com.jimmy.cleanarchitecture.domain.Document

/**
 * Adding a bookmark to a currently open document use case
 * Each use case class has only one function that invokes the use case.
 * For convenience, you’re overloading the invoke operator.
 * This enables you to simplify the function call on AddBookmark instance to addBookmark()
 * instead of addBookmark.invoke().
 */
class AddBookmark (private val bookmarkRepository: BookmarkRepository) {
    suspend operator fun invoke(document: Document, bookmark: Bookmark) =
        bookmarkRepository.addBookmark(document, bookmark)
}