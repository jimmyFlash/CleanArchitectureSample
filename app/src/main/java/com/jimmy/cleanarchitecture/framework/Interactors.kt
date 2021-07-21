package com.jimmy.cleanarchitecture.framework

import com.jimmy.cleanarchitecture.interactors.*

/*
To keep things simple you’ll manually implement an easy way to provide dependencies
 to your ViewModels
 use it to access interactors from ViewModels
 */
class Interactors(val addBookmark: AddBookmark,
                  val getBookmarks: GetBookmarks,
                  val deleteBookmark: RemoveBookmark,
                  val addDocument: AddDocument,
                  val getDocuments: GetDocuments,
                  val removeDocument: RemoveDocument,
                  val getOpenDocument: GetOpenDocument,
                  val setOpenDocument: SetOpenDocument)