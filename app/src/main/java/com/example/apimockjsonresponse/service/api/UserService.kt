package com.example.apimockjsonresponse.service.api

import com.example.apimockjsonresponse.response.User
import retrofit2.Call
import retrofit2.http.GET

interface UserService {

    @GET("/users")
    fun getUser(): Call<List<User>>

    @GET("/tests")
    fun getTest(): Call<Boolean>
}