package com.example.apimockjsonresponse.service.provider

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonProvider {
    fun getInstance(): Gson {
        return  GsonBuilder().setLenient().create()
    }
}