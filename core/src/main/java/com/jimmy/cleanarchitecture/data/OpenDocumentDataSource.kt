package com.jimmy.cleanarchitecture.data

import com.jimmy.cleanarchitecture.domain.Document

/**
 * This data source will take care of storing and retrieving
 * the currently opened PDF document, concrete implementation found
 * in framework layer
 */
interface OpenDocumentDataSource {

    fun setOpenDocument(document: Document)

    fun getOpenDocument(): Document
}