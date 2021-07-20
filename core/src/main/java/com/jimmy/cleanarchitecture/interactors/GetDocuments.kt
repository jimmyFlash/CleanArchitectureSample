package com.jimmy.cleanarchitecture.interactors

import com.jimmy.cleanarchitecture.data.DocumentRepository

/**
 * Getting all documents in library use case
 * Each use case class has only one function that invokes the use case.
 * For convenience, you’re overloading the invoke operator.
 * This enables you to simplify the function call on GetDocuments instance to getDocuments()
 * instead of getDocuments.invoke().
 */
class GetDocuments (private val documentRepository: DocumentRepository) {
    suspend operator fun invoke() = documentRepository.getDocuments()
}