package com.example.apimockjsonresponse.response

import com.google.gson.annotations.SerializedName

data class MockResponse(
    @SerializedName("method")
    val method: String?,
    @SerializedName("status")
    val status: Int? = 200,
    @SerializedName("url")
    val url: String?,
    @SerializedName("response")
    val response: Any?
)