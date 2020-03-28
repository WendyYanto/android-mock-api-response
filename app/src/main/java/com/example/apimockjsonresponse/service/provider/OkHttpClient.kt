package com.example.apimockjsonresponse.service.provider

import android.content.Context
import com.example.apimockjsonresponse.service.interceptor.MockResponseInterceptor
import okhttp3.OkHttpClient

class OkHttpClientProvider {

    companion object {

        private var client: OkHttpClient? = null

        fun init(context: Context) {
            client = OkHttpClient
                .Builder()
                .addInterceptor(MockResponseInterceptor(context))
                .build()
        }

        fun getInstance(): OkHttpClient {
            return client!!
        }

    }

}