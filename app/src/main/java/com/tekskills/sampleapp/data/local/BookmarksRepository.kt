package com.tekskills.sampleapp.data.local

class BookmarksRepository(private val dao: BookmarkDAO){

    val bookmarks = dao.getAllBookmarks()

    suspend fun insertArticleIntoBookmarks(bookmark: BookmarksAllNews){
        dao.insertArticle(bookmark)
    }

    suspend fun deleteArticlefromBookmarks(bookmark: BookmarksAllNews){
        dao.deleteArticle(bookmark)
    }

    suspend fun deleteALlArticleFromBookmarks(){
        dao.deleteAll()
    }
}