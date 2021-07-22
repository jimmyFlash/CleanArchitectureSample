/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jimmy.cleanarchitecture.presentation.reader

import android.app.Application
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import com.jimmy.cleanarchitecture.domain.Bookmark
import com.jimmy.cleanarchitecture.domain.Document
import com.jimmy.cleanarchitecture.framework.Interactors
import com.jimmy.cleanarchitecture.framework.MajesticViewModel
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Here’s an outline of the ReaderViewModel with functions that ReaderFragment will call on user actions:
 * openDocument(): Opens the PDF document.
 * openBookmark(): Navigates to the given bookmark in the document.
 * openPage(): Opens a given page in the document.
 * nextPage(): Navigates to the next page.
 * previousPage(): Navigates to the previous page.
 * toggleBookmark(): Adds or removes the current page from document bookmarks.
 * toggleInLibrary(): Adds or removes the open document from the library.
 * ReaderFragment will get a Document to display as an argument when it’s created.
 *
 */
class ReaderViewModel(application: Application, interactors: Interactors) : MajesticViewModel
(application, interactors) {

  companion object {
    private const val DOCUMENT_ARG = "document"

    fun createArguments(document: Document) = bundleOf(
        DOCUMENT_ARG to document
    )
  }

  val document = MutableLiveData<Document>()

  /*
  This will change the value of bookmarks each time you change the document.
   It will fill with up to date bookmarks, which you get from the interactors,
   within a coroutine
   The document holds the document parsed from Fragment arguments.
   bookmarks holds the list of bookmarks in the current document.
   ReaderFragment will subscribe to it to get the list of available bookmarks
   */
  val bookmarks = MediatorLiveData<List<Bookmark>>().apply {
    addSource(document) { document ->
      viewModelScope.launch {
        postValue(interactors.getBookmarks(document))
      }
    }
  }

  // holds the reference to PdfRenderer.Page that you currently display, if any
  val currentPage = MediatorLiveData<PdfRenderer.Page>()

  // LiveData transformations. returns true if the index of currentPage is larger than zero.
  val hasPreviousPage: LiveData<Boolean> = Transformations.map(currentPage) {
    it.index > 0
  }

  // LiveData transformations. returns true if the index of currentPage is less than
  // the page count minus one – if the user hasn’t reached the end.
  // This data then dictates how the UI should appear and behave, in the ReaderFragment.
  val hasNextPage: LiveData<Boolean> = Transformations.map(currentPage) {
    renderer.value?.let { renderer -> it.index < renderer.pageCount - 1 }
  }

  val isBookmarked = MediatorLiveData<Boolean>().apply {
    addSource(document) { value = isCurrentPageBookmarked() }
    addSource(currentPage) { value = isCurrentPageBookmarked() }
    addSource(bookmarks) { value = isCurrentPageBookmarked() }
  }

  val isInLibrary: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
    addSource(document) {
        document -> viewModelScope.launch {
          postValue(isInLibrary(document))
        }
    }
  }

  /*
  holds a reference to the PdfRenderer used for rendering the document.
  Each time you change the document‘s internal value,
  you create a new instance of PdfRenderer for the document and store in the
   */
  val renderer = MediatorLiveData<PdfRenderer>().apply {
    addSource(document) {
      try {
        val pdfRenderer = PdfRenderer(getFileDescriptor(Uri.parse(it.url))!!)
        value = pdfRenderer
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
  }

  private fun getFileDescriptor(uri: Uri) = application.contentResolver.openFileDescriptor(uri, "r")

  // This will use GetDocuments to get a list of all documents in the library
  // and check if it contains one that matches the currently open document
  private fun isCurrentPageBookmarked() =
      bookmarks.value?.any { it.page == currentPage.value?.index } == true

  // It should return true if the open document is already in the library
  private suspend fun isInLibrary(document: Document) =
    interactors.getDocuments().any { it.url == document.url }

  fun loadArguments(arguments: Bundle?) {
    if (arguments == null) {
      return
    }

    // Initializes currentPage to be set to the first page or first bookmarked page if it exists.
    currentPage.apply {
      addSource(renderer) { renderer ->
        viewModelScope.launch {
          val document = document.value

          if (document != null) {
            val bookmarks = interactors.getBookmarks(document).lastOrNull()?.page ?: 0
            postValue(renderer.openPage(bookmarks))
          }
        }
      }
    }
    // Gets Document passed to ReaderFragment.
    val documentFromArguments = arguments.get(DOCUMENT_ARG) as Document? ?: Document.EMPTY

    // Gets the last document that was opened from GetOpenDocument.
    val lastOpenDocument = interactors.getOpenDocument()

    // Sets the value of document to the one passed to ReaderFragment or
    // falls back to lastOpenDocument if nothing was passed
    document.value = when {
      documentFromArguments != Document.EMPTY -> documentFromArguments
      documentFromArguments == Document.EMPTY && lastOpenDocument != Document.EMPTY -> lastOpenDocument
      else -> Document.EMPTY
    }

    // Sets the new open document by calling SetOpenDocument
    document.value?.let { interactors.setOpenDocument(it) }
  }

  /**
   * This creates a new Document that represents the document
   * that was just open and passes it to SetOpenDocument.
   *
   */
  fun openDocument(uri: Uri) {
    document.value = Document(uri.toString(), "", 0, "")
    document.value?.let { interactors.setOpenDocument(it) }
  }

  fun openBookmark(bookmark: Bookmark) {
    openPage(bookmark.page)
  }

  private fun openPage(page: Int) = renderer.value?.let {
    currentPage.value = it.openPage(page)
  }

  fun nextPage() = currentPage.value?.let { openPage(it.index.plus(1)) }

  fun previousPage() = currentPage.value?.let { openPage(it.index.minus(1)) }

  fun reopenPage() = openPage(currentPage.value?.index ?: 0)

  /**
   * either delete or add a bookmark, depending on if it’s already in your database,
   * and then you update the bookmarks, to refresh the UI.
   *
   */
  fun toggleBookmark() {
    val currentPage = currentPage.value?.index ?: return
    val document = document.value ?: return
    val bookmark = bookmarks.value?.firstOrNull { it.page == currentPage }

    viewModelScope.launch {
      if (bookmark == null) {
        interactors.addBookmark(document, Bookmark(page = currentPage))
      } else {
        interactors.deleteBookmark(document, bookmark)
      }

      bookmarks.postValue(interactors.getBookmarks(document))
    }
  }

  fun toggleInLibrary() {
    val document = document.value ?: return

    viewModelScope.launch {
      if (isInLibrary.value == true) {
        interactors.removeDocument(document)
      } else {
        interactors.addDocument(document)
      }

      isInLibrary.postValue(isInLibrary(document))
    }
  }
}
