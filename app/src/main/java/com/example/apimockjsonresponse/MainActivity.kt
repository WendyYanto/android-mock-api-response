package com.example.apimockjsonresponse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.apimockjsonresponse.response.User
import com.example.apimockjsonresponse.service.provider.RetrofitProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val TAG =  this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RetrofitProvider.getUserService().getUser().enqueue(object: Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e(TAG, t.message.orEmpty())
            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                Log.i(TAG, response.body().toString())
            }
        })
    }
}
