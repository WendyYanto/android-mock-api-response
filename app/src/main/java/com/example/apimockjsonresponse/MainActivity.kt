package com.example.apimockjsonresponse

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
                Log.i(TAG, "Success FetchUsers Response")
                Log.i(TAG, response.body().toString())
            }
        })

        RetrofitProvider.getUserService().getTest().enqueue(object: Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e(TAG, t.message.orEmpty())
            }

            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.i(TAG, "Success FetchTesting Response")
                Log.i(TAG, response.body().toString())
            }

        })
    }
}
