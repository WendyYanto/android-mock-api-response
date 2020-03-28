package com.example.apimockjsonresponse.service.provider

import com.example.apimockjsonresponse.service.api.UserService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitProvider() {

    companion object {
        val BASE_URL = "https://jsonplaceholder.typicode.com"
        private var retrofit: Retrofit? = null

        private fun getInstance(): Retrofit {
            if (retrofit == null) {
                val gson = GsonBuilder().setLenient().create()
                retrofit = Retrofit.Builder()
                    .client(OkHttpClientProvider.getInstance())
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit!!
        }

        fun getUserService(): UserService {
            return getInstance().create(UserService::class.java)
        }
    }

}