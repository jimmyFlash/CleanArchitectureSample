package com.jimmy.cleanarchitecture.data

import com.jimmy.cleanarchitecture.domain.Document

/**
 * This data source will take care of adding, querying and removing
 * document from the database
 */
interface DocumentDataSource {

    suspend fun add(document: Document)

    suspend fun readAll(): List<Document>

    suspend fun remove(document: Document)
}