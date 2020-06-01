package dev.wendyyanto.mockinterceptor.interceptor

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.wendyyanto.mockinterceptor.interceptor.response.MockResponse
import okhttp3.Interceptor
import java.nio.charset.Charset
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.AtomicReference

class MockResponseInterceptor private constructor(
    private val context: Context,
    private val objectMapper: Gson,
    private val url: String,
    private val file: String
) : Interceptor {

    data class Builder(
        var context: Context? = null,
        var objectMapper: Gson? = null,
        var url: String? = null,
        var file: String = "data"
    ) {
        fun context(context: Context) = apply { this.context = context }
        fun objectMapper(objectMapper: Gson) = apply { this.objectMapper = objectMapper }
        fun url(url: String) = apply { this.url = url }
        fun file(file: String) = apply { this.file = file }
        fun build() = MockResponseInterceptor(context!!, objectMapper!!, url!!, file)
    }

    private val mockResponse = AtomicReference<MutableMap<String, MockResponse>>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val endpoint = chain.request().url.toString().replace(url, "")
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
        try {
            val jsonInputStream = context.assets.open("$file.json")
            val size = jsonInputStream.available()
            val buffer = ByteArray(size)
            jsonInputStream.read(buffer)
            jsonInputStream.close()
            val jsonResponse = String(buffer, Charset.defaultCharset())
            val mockResponseType = object : TypeToken<List<MockResponse>>() {}.type
            val response = objectMapper.fromJson<List<MockResponse>>(jsonResponse, mockResponseType)
            mockResponse.set(toResponseWithQueryParams(response))
        } catch (e: java.lang.Exception) {
            throw IllegalArgumentException("Cannot read JSON from file $file")
        }
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

