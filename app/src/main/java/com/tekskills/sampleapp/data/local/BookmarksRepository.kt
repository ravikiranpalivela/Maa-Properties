package com.tekskills.sampleapp.data.local

class BookmarksRepository(private val dao: BookmarkDAO){

    val bookmarks = dao.getAllBookmarks()

    suspend fun insertArticleIntoBookmarks(bookmark: BookmarksAllNews){
        dao.insertArticle(bookmark)
    }

    suspend fun deleteArticleFromBookmarks(bookmark: BookmarksAllNews){
        dao.deleteArticle(bookmark)
    }

    suspend fun deleteALlArticleFromBookmarks(){
        dao.deleteAll()
    }

    suspend fun getBookMarkViewCount(articleId: Int): BookMarkViewCount {
        return dao.getBookmarkCountById(articleId)
    }

    suspend fun insertOrUpdateBookmark(id: Int = 0, news_id: Int, viewCount: Int) {
        val existingBookmark = dao.getBookmarkCountById(news_id)
        if (existingBookmark != null) {
            // Update the existing article
            var count = existingBookmark.view_count+1
            dao.incrementViewCount(existingBookmark.news_id,count)
        } else {
            val bookMark = BookmarksAllNews(id, news_id, viewCount)
            // Insert a new article
            dao.insertBookmark(bookMark)
        }
    }

    suspend fun doesBookmarkExist(articleId: Int): Boolean {
        return dao.getBookmarkById(articleId) != null
    }
}