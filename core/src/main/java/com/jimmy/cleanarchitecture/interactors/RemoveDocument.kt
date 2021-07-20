package com.jimmy.cleanarchitecture.interactors

import com.jimmy.cleanarchitecture.data.DocumentRepository
import com.jimmy.cleanarchitecture.domain.Document

/**
 * remove a document from library use case
 * Each use case class has only one function that invokes the use case.
 * For convenience, you’re overloading the invoke operator.
 * This enables you to simplify the function call on RemoveDocument instance to removeDocument()
 * instead of removeDocument.invoke().
 */
class RemoveDocument(private val documentRepository: DocumentRepository) {
    suspend operator fun invoke(document: Document) =
        documentRepository.removeDocument(document)
}