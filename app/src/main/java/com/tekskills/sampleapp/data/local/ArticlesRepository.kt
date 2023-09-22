package com.tekskills.sampleapp.data.local

class ArticlesRepository(private val dao: ArticleDAO){

    val articles = dao.getAllArticles()

    suspend fun insertArticleIntoArticles(bookmark: ArticlesAllNews){
        dao.insertArticle(bookmark)
    }

    suspend fun deleteArticleFromArticles(bookmark: ArticlesAllNews){
        dao.deleteArticle(bookmark)
    }

    suspend fun deleteALlArticleFromArticles(){
        dao.deleteAll()
    }

    suspend fun getArticleViewCount(articleId: Int): ArticleViewCount {
        return dao.getArticleCountById(articleId)
    }

    suspend fun insertOrUpdateArticleViewCount(id: Int = 0, news_id: Int, viewCount: Int) {
        val existingArticle = dao.getArticleCountById(news_id)
        if (existingArticle != null) {
            // Update the existing article
            var count = existingArticle.view_count+1
            dao.incrementViewCount(existingArticle.news_id,count)
        } else {
            val article = ArticlesAllNews(id, news_id, viewCount)
            // Insert a new article
            dao.insertArticle(article)
        }
    }

    suspend fun insertOrUpdateArticleShareCount(id: Int = 0, news_id: Int, viewCount: Int) {
        val existingArticle = dao.getArticleCountById(news_id)
        if (existingArticle != null) {
            // Update the existing article
            var count = existingArticle.share_count+1
            dao.incrementShareCount(existingArticle.news_id,count)
        } else {
            val article = ArticlesAllNews(id, news_id, share_count = viewCount)
            // Insert a new article
            dao.insertArticle(article)
        }
    }


    suspend fun doesArticleExist(articleId: Int): Boolean {
        return dao.getArticleById(articleId) != null
    }
}