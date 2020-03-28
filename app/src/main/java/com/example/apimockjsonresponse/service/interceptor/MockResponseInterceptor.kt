package com.example.apimockjsonresponse.service.interceptor

import android.content.Context
import com.example.apimockjsonresponse.response.MockResponse
import com.example.apimockjsonresponse.service.provider.RetrofitProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.nio.charset.Charset
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.concurrent.atomic.AtomicReference

class MockResponseInterceptor(private val context: Context) : Interceptor {

    companion object {
        private const val MOCK_JSON_FILE = "data.json"
    }

    private val objectMapper = Gson()
    private val mockResponse = AtomicReference<MutableList<MockResponse>>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val endpoint = chain.request().url.toString().replace(RetrofitProvider.BASE_URL, "")
        val method = chain.request().method
        val response = getMockResponse(endpoint, method)
        return chain.proceed(chain.request())
            .newBuilder()
            .protocol(Protocol.HTTP_2)
            .code(response.second ?: 200)
            .body(
                response.first.toByteArray().toResponseBody("application/json; charset=utf-8".toMediaType())
            )
            .addHeader("content-type", "application/json")
            .build()
    }

    private fun getMockResponse(endpoint: String, method: String): Pair<String, Int?> {
        return try {
            this.fetchMockResponse()
            val response = mockResponse.get().find {
                it.method == method && it.url == endpoint
            }
            this.objectMapper.toJson(response?.response) to response?.status
        } catch (e: Exception) {
            "" to 404
        }
    }

    private fun fetchMockResponse() {
        if (mockResponse.get().isNullOrEmpty()) {
            val jsonInputStream = context.assets.open(MOCK_JSON_FILE)
            val size = jsonInputStream.available()
            val buffer = ByteArray(size)
            jsonInputStream.read(buffer)
            jsonInputStream.close()
            val json = String(buffer, Charset.defaultCharset())
            val response = this.objectMapper.fromJson<List<MockResponse>>(json)
            mockResponse.set(toResponseWithQueryParams(response))
        }
    }

    private fun toResponseWithQueryParams(response: List<MockResponse>): MutableList<MockResponse> {
        return response.map {
            it.queries?.let { queries ->
                it.url += "?"
                queries.forEach { query ->
                    it.url += "${query.key}=${query.value}&"
                }
                val length = it.url?.length ?: 1
                it.url = it.url?.substring(0, length - 1)
            }
            it
        }.toMutableList()
    }
}

inline fun <reified T> Gson.fromJson(json: String) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)
