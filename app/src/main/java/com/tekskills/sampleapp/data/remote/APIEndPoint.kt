package com.tekskills.sampleapp.data.remote

class APIEndPoint {
    companion object {
        const val GET_WISHES_LIST = "wish-news-two/get-all"
        //        const val GET_MAIN_NEWS_LIST = "api/main-news"
        const val GET_MAIN_NEWS_LIST = "api/main-news/all/"
        const val GET_POSTERS_LIST = "poster"
        const val GET_ALL_NEWS_LIST = "api/common/all"
        const val GET_SHORT_LIST = "api/short/getall"
        const val GET_BANNER_LIST = "api/advertise/active"
        const val GET_PUBLIC_ADS = "public-add"
        const val POST_COMMENTS = "https://admin2.maaproperties.com/api/common"
        const val POST_VOTE = "https://admin2.maaproperties.com/poll/add-user-poll"
        const val PUT_LIKES_COMMON = "https://admin2.maaproperties.com/api/common"
        const val PUT_SHARE_COMMON = "https://admin2.maaproperties.com/api/common/share"
        const val BASE_URL = "http://admin2.maaproperties.com/"
    }
}