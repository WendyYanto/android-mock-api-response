package com.example.apimockjsonresponse.service.interceptor

import android.content.Context
import com.example.apimockjsonresponse.BuildConfig
import com.example.apimockjsonresponse.response.MockResponse
import com.example.apimockjsonresponse.service.provider.RetrofitProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Interceptor
import java.nio.charset.Charset
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.concurrent.atomic.AtomicReference

class MockResponseInterceptor(private val context: Context) : Interceptor {

    companion object {
        private const val MOCK_JSON_FILE = "data.json"
    }

    private val objectMapper = Gson()
    private val mockResponse = AtomicReference<MutableMap<String, MockResponse>>()

    override fun intercept(chain: Interceptor.Chain): Response {
        if (BuildConfig.DEBUG) {
            val endpoint = chain.request().url.toString().replace(RetrofitProvider.BASE_URL, "")
            val method = chain.request().method
            val response = fetchMockResponse(endpoint, method)
            return chain.proceed(chain.request())
                .newBuilder()
                .protocol(Protocol.HTTP_2)
                .code(response.second ?: 200)
                .body(response.first.toByteArray().toResponseBody("application/json".toMediaType()))
                .addHeader("content-type", "application/json")
                .build()
        }
        return chain.proceed(chain.request())
    }

    private fun fetchMockResponse(endpoint: String, method: String): Pair<String, Int?> {
        return try {
            if (mockResponse.get().isNullOrEmpty()) {
                this.fillMockResponse()
            }
            val response = mockResponse.get()["${endpoint}_${method}"]
            this.objectMapper.toJson(response?.response) to response?.status
        } catch (e: Exception) {
            "" to 404
        }
    }

    private fun fillMockResponse() {
        val jsonInputStream = context.assets.open(MOCK_JSON_FILE)
        val size = jsonInputStream.available()
        val buffer = ByteArray(size)
        jsonInputStream.read(buffer)
        jsonInputStream.close()
        val jsonResponse = String(buffer, Charset.defaultCharset())
        val mockResponseType = object : TypeToken<List<MockResponse>>() {}.type
        val response = objectMapper.fromJson<List<MockResponse>>(jsonResponse, mockResponseType)
        mockResponse.set(toResponseWithQueryParams(response))
    }

    private fun toResponseWithQueryParams(response: List<MockResponse>): MutableMap<String, MockResponse> {
        val responseMap = mutableMapOf<String, MockResponse>()
        response.forEach {
            it.queries?.let { queries ->
                it.url += "?"
                queries.forEach { query ->
                    it.url += "${query.key}=${query.value}&"
                }
                val length = it.url?.length ?: 1
                it.url = it.url?.substring(0, length - 1)
            }
            responseMap["${it.url}_${it.method}"] = it
        }
        return responseMap
    }

}

