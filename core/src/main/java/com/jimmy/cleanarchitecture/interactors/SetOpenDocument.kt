package com.jimmy.cleanarchitecture.interactors

import com.jimmy.cleanarchitecture.data.DocumentRepository
import com.jimmy.cleanarchitecture.domain.Document

/**
 * Setting currently opened documents use case
 * Each use case class has only one function that invokes the use case.
 * For convenience, you’re overloading the invoke operator.
 * This enables you to simplify the function call on SetOpenDocument instance to setOpenDocument()
 * instead of setOpenDocument.invoke().
 */
class SetOpenDocument (private val documentRepository: DocumentRepository) {
    operator fun invoke(document: Document) =
        documentRepository.setOpenDocument(document)
}