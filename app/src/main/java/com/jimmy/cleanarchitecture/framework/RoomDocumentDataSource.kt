package com.jimmy.cleanarchitecture.framework

import android.content.Context
import com.jimmy.cleanarchitecture.data.DocumentDataSource
import com.jimmy.cleanarchitecture.domain.Document
import com.jimmy.cleanarchitecture.framework.db.DocumentEntity
import com.jimmy.cleanarchitecture.framework.db.MajesticReaderDatabase

class RoomDocumentDataSource (val context: Context) : DocumentDataSource {

    // call DAO implementation to handle DB operations
    private val documentDao = MajesticReaderDatabase.getInstance(context).documentDao()

    // insert document object entity to db document column
    override suspend fun add(document: Document) {
        val details = FileUtil.getDocumentDetails(context, document.url)
        documentDao.addDocument(
            DocumentEntity(document.url, details.name, details.size, details.thumbnail)
        )
    }

    // return a list of Document objects stored in db
    override suspend fun readAll(): List<Document> = documentDao.getDocuments().map {
        Document(
            it.uri,
            it.title,
            it.size,
            it.thumbnailUri
        )
    }

    // remove document record from db
    override suspend fun remove(document: Document) = documentDao.removeDocument(
        DocumentEntity(document.url, document.name, document.size, document.thumbnail)
    )
}