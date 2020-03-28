package com.example.apimockjsonresponse.service.api

import com.example.apimockjsonresponse.response.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {

    @GET("/users")
    fun getUser(): Call<List<User>>

    @GET("/tests")
    fun getTest(@Query("page") page: Int = 0, @Query("size") size: Int = 10): Call<Boolean>
}