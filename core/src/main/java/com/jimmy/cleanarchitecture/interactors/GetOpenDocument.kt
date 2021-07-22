package com.jimmy.cleanarchitecture.interactors

import com.jimmy.cleanarchitecture.data.DocumentRepository

class GetOpenDocument(private val documentRepository: DocumentRepository) {
    operator fun invoke() = documentRepository.getOpenDocument()
}