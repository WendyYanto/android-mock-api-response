package com.example.apimockjsonresponse.response

import com.google.gson.annotations.SerializedName

data class MockResponse(
    @SerializedName("method")
    val method: String?,
    @SerializedName("status")
    val status: Int? = 200,
    @SerializedName("url")
    var url: String?,
    @SerializedName("query_params")
    val queries: Map<String, String>?,
    @SerializedName("response")
    val response: Any?
)