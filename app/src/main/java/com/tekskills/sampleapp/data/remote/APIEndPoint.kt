package com.tekskills.sampleapp.data.remote

class APIEndPoint {
    companion object {
        const val GET_WISHES_LIST = "wish-news-two/get-all"
        const val GET_MAIN_NEWS_LIST = "api/main-news"
        const val GET_POSTERS_LIST = "poster"
        const val GET_ALL_NEWS_LIST = "api/common/all"
        const val GET_SHORT_LIST = "api/short/getall"
        const val GET_BANNER_LIST = "api/advertise/active"
        const val POST_NEWS_LIKE = "api/main-news/like/"
        const val POST_WISHES_LIKE = "wish-news-two/like/"
        const val POST_SHORTS_LIKE = "api/short/like/"
        const val POST_POSTER_LIKE = "poster/like/"
        const val POST_COMMENTS = "api/common"
        const val PUT_LIKES_COMMON="api/common"
    }
}