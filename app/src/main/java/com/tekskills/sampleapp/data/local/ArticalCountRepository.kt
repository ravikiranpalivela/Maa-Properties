package com.tekskills.sampleapp.data.local

class ArticalCountRepository(private val dao: ArticalCountDAO) {

    val bookmarks = dao.getAllArticalCount()

    suspend fun insertArticleCount(bookmark: ArticalCount) {
        dao.insertArticleCount(bookmark)
    }

    suspend fun deleteArticleCount(bookmark: ArticalCount) {
        dao.deleteArticle(bookmark)
    }

    suspend fun deleteALlArticleCount() {
        dao.deleteAll()
    }

    suspend fun insertArticle(article: ArticalCount) {
        dao.insertArticleCount(article)
    }

    suspend fun getArticleViewCount(articleId: String): Int {
        val article = dao.getArticleCountById(articleId)
        return article?.viewCount ?: 0
    }

    suspend fun incrementViewCount(articleId: String) {
        val article = dao.getArticleCountById(articleId)
        article?.let {
            it.viewCount++
            dao.updateArticleCount(it)
        }
    }
}