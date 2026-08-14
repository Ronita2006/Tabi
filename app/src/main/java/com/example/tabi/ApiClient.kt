package com.example.tabi

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object ApiClient {

    private val client = OkHttpClient()

    // Android emulator -> your computer's localhost
    private const val BASE_URL = "http://127.0.0.1:8787"

    fun register(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {

        val json = JSONObject().apply {
            put("name", name)
            put("email", email)
            put("password", password)
        }

        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/api/register")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(
                call: okhttp3.Call,
                e: java.io.IOException
            ) {
                onResult(false, "Network error: ${e.message}")
            }

            override fun onResponse(
                call: okhttp3.Call,
                response: okhttp3.Response
            ) {

                val responseBody = response.body?.string()

                try {

                    val jsonResponse =
                        JSONObject(responseBody ?: "{}")

                    val success =
                        jsonResponse.optBoolean("success")

                    val message =
                        jsonResponse.optString(
                            "message",
                            "Unknown error"
                        )

                    onResult(success, message)

                } catch (e: Exception) {

                    onResult(
                        false,
                        "Invalid server response"
                    )
                }
            }
        })
    }

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {

        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/api/login")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(
                call: okhttp3.Call,
                e: java.io.IOException
            ) {
                onResult(false, "Network error: ${e.message}")
            }

            override fun onResponse(
                call: okhttp3.Call,
                response: okhttp3.Response
            ) {

                val responseBody = response.body?.string()

                try {

                    val jsonResponse =
                        JSONObject(responseBody ?: "{}")

                    val success =
                        jsonResponse.optBoolean("success")

                    val message =
                        jsonResponse.optString(
                            "message",
                            "Unknown error"
                        )

                    onResult(success, message)

                } catch (e: Exception) {

                    onResult(
                        false,
                        "Invalid server response"
                    )
                }
            }
        })
    }
}

