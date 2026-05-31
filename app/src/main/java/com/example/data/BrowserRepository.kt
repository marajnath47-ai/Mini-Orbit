package com.example.data

import kotlinx.coroutines.flow.Flow

class BrowserRepository(private val dao: BrowserDao) {
    val allBookmarks: Flow<List<Bookmark>> = dao.getAllBookmarks()
    val allHistory: Flow<List<HistoryItem>> = dao.getAllHistory()

    suspend fun addBookmark(bookmark: Bookmark) {
        dao.insertBookmark(bookmark)
    }

    suspend fun removeBookmarkByUrl(url: String) {
        dao.deleteBookmarkByUrl(url)
    }

    suspend fun removeBookmarkById(id: Int) {
        dao.deleteBookmarkById(id)
    }

    suspend fun addHistory(historyItem: HistoryItem) {
        dao.insertHistory(historyItem)
    }

    suspend fun removeHistoryById(id: Int) {
        dao.deleteHistoryById(id)
    }

    suspend fun clearAllHistory() {
        dao.clearHistory()
    }
}
