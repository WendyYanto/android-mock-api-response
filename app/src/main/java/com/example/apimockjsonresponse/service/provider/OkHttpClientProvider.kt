package com.example.apimockjsonresponse.service.provider

import android.content.Context
import dev.wendyyanto.mockinterceptor.interceptor.MockResponseInterceptor
import okhttp3.OkHttpClient

class OkHttpClientProvider {

    companion object {

        private var client: OkHttpClient? = null

        fun init(context: Context) {
            client = OkHttpClient
                .Builder()
                .addInterceptor(
                    MockResponseInterceptor.Builder()
                        .context(context)
                        .objectMapper(GsonProvider.getInstance())
                        .url(RetrofitProvider.BASE_URL)
                        .build()
                )
                .build()
        }

        fun getInstance(): OkHttpClient {
            return client!!
        }

    }

}