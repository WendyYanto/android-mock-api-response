package com.example.apimockjsonresponse

import android.app.Application
import com.example.apimockjsonresponse.service.provider.OkHttpClientProvider

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        OkHttpClientProvider.init(this)
    }
}