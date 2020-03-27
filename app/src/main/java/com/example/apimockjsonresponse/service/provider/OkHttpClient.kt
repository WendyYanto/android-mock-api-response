package com.example.apimockjsonresponse.service.provider

import okhttp3.OkHttpClient

class OkHttpClientProvider {

    companion object {

        private var client: OkHttpClient? = null

        fun getInstance(): OkHttpClient {
            if (client == null) {
                client = OkHttpClient.Builder().build()
            }
            return client!!
        }

    }

}