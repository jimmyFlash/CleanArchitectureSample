package com.jimmy.cleanarchitecture.interactors

import com.jimmy.cleanarchitecture.data.DocumentRepository
import com.jimmy.cleanarchitecture.domain.Document

/**
 * Adding a document to the library use case
 * Each use case class has only one function that invokes the use case.
 * For convenience, you’re overloading the invoke operator.
 * This enables you to simplify the function call on AddDocument instance to addDocument()
 * instead of addDocument.invoke().
 */
class AddDocument (private val documentRepository: DocumentRepository) {
    suspend operator fun invoke(document: Document) =
        documentRepository.addDocument(document)
}