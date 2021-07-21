package com.jimmy.cleanarchitecture.framework

import com.jimmy.cleanarchitecture.data.OpenDocumentDataSource
import com.jimmy.cleanarchitecture.domain.Document

/**
 * This is an implementation of OpenDocumentDataSource from the Data layer.
 * Currently, the open document is stored in memory,
 * so the implementation is pretty straightforward.
 */
class InMemoryOpenDocumentDataSource : OpenDocumentDataSource {

    private var openDocument: Document = Document.EMPTY

    override fun setOpenDocument(document: Document) {
        openDocument = document
    }

    override fun getOpenDocument(): Document= openDocument
}