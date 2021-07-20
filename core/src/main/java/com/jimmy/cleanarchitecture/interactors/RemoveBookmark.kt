package com.jimmy.cleanarchitecture.interactors

import com.jimmy.cleanarchitecture.data.BookmarkRepository
import com.jimmy.cleanarchitecture.domain.Bookmark
import com.jimmy.cleanarchitecture.domain.Document

/**
 * remove a bookmark from open document use case
 * Each use case class has only one function that invokes the use case.
 * For convenience, you’re overloading the invoke operator.
 * This enables you to simplify the function call on RemoveBookmark instance to removeBookmark()
 * instead of removeBookmark.invoke().
 */
class RemoveBookmark (private val bookmarksRepository: BookmarkRepository) {
    suspend operator fun invoke(document: Document, bookmark: Bookmark) =
        bookmarksRepository.removeBookmark(document, bookmark)
}