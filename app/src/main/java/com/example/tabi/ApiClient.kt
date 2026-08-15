package com.example.tabi

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object ApiClient {

    private val client = OkHttpClient()

    // Android emulator -> your computer's localhost
    //private const val BASE_URL = "http://127.0.0.1:8787"
    private const val BASE_URL = "http://10.0.2.2:8787"

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
/*package com.example.tabi

import android.os.Handler
import android.os.Looper

object ApiClient {

    // 🔒 Local memory database simulating an isolated server storage system
    private val localUserDatabase = mutableMapOf<String, String>()

    fun register(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        // Simulate a 1-second network processing delay for visual loading effects
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                name.isBlank() || email.isBlank() || password.isBlank() -> {
                    onResult(false, "Registration Failed: Fields cannot be empty.")
                }
                localUserDatabase.containsKey(email) -> {
                    onResult(false, "Registration Failed: Email already registered.")
                }
                else -> {
                    // Save the user inside your local memory sandbox
                    localUserDatabase[email] = password
                    onResult(true, "Success: Account created inside local sandbox!")
                }
            }
        }, 1000)
    }

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                email.isBlank() || password.isBlank() -> {
                    onResult(false, "Login Failed: Missing email or password.")
                }
                // Testing backdoor backdoor shortcut credential so you can always log in instantly
                email == "test@test.com" && password == "Password123!" -> {
                    onResult(true, "Success: Logged in via master test user.")
                }
                localUserDatabase[email] == password -> {
                    onResult(true, "Success: Welcome back!")
                }
                else -> {
                    onResult(false, "Login Failed: Invalid credentials.")
                }
            }
        }, 1000)
    }
}
*/



