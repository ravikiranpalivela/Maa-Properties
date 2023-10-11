package com.tekskills.sampleapp.data.remote

import com.google.gson.GsonBuilder
import com.tekskills.sampleapp.data.remote.APIEndPoint.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    companion object {
        fun getRetrofitInstance(): Retrofit {
            return Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(getRequestResponseInterceptor())
                .build()
        }

        /**
         * Custom Response Interceptor client that intercepts the service calls and provides insights into
         * every different processes taking place.
         *
         * This method is used for the following use cases:
         *      -> Intercepting the key-value pairs in the data.
         *      -> Intercepting the response body of the service call.
         *      -> Intercepting the download progress of data being downloaded.
         *
         * Returns customized [OkHttpClient] object.
         */
        private fun getRequestResponseInterceptor(): OkHttpClient {

            val httpClient: OkHttpClient.Builder = OkHttpClient().newBuilder()
            val logging = HttpLoggingInterceptor()

            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.interceptors().add(logging)
//        httpClient.addNetworkInterceptor(NetworkInterceptor())
            httpClient.connectTimeout(30, TimeUnit.SECONDS)
            httpClient.writeTimeout(25, TimeUnit.SECONDS)
            httpClient.readTimeout(20, TimeUnit.MINUTES)
            return httpClient.build()
        }
    }
}