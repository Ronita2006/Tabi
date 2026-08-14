package com.example.tabi

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

data class RemoteTripPackage(
    val id: String,
    val title: String,
    val route: String,
    val days: Int,
    val price: Int,
    val rating: Double,
    val style: String,
    val highlights: List<String>
)

object TripRecommendationApi {

    // Android Emulator -> Windows host
    private const val BASE_URL = "http://127.0.0.1:8787"

    private val executor = Executors.newCachedThreadPool()

    fun recommendTrips(
        days: Int,
        travelers: Int,
        budgetPerTraveler: Int?,
        style: String,
        callback: (Result<List<RemoteTripPackage>>) -> Unit
    ) {
        executor.execute {

            var connection: HttpURLConnection? = null

            try {

                val url = URL("$BASE_URL/api/recommendations")

                connection =
                    url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.doOutput = true
                connection.useCaches = false

                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                // =========================
                // REQUEST BODY
                // =========================

                val body = JSONObject()

                body.put("days", days)
                body.put("travelers", travelers)
                body.put("style", style)

                if (budgetPerTraveler != null) {
                    body.put(
                        "budgetPerTraveler",
                        budgetPerTraveler
                    )
                } else {
                    body.put(
                        "budgetPerTraveler",
                        JSONObject.NULL
                    )
                }

                val requestBody = body.toString()

                println(
                    "TABi API REQUEST: $requestBody"
                )

                // =========================
                // SEND REQUEST
                // =========================

                connection.outputStream.use { output ->
                    output.write(
                        requestBody.toByteArray(
                            Charsets.UTF_8
                        )
                    )

                    output.flush()
                }

                // =========================
                // RESPONSE
                // =========================

                val responseCode =
                    connection.responseCode

                val responseStream =
                    if (responseCode in 200..299) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    }

                val responseText =
                    responseStream
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        ?: ""

                println(
                    "TABi API RESPONSE CODE: $responseCode"
                )

                println(
                    "TABi API RESPONSE: $responseText"
                )

                // =========================
                // SERVER ERROR
                // =========================

                if (responseCode !in 200..299) {

                    throw Exception(
                        "Backend returned HTTP $responseCode\n\n$responseText"
                    )
                }

                // =========================
                // PARSE JSON
                // =========================

                val json =
                    JSONObject(responseText)

                val success =
                    json.optBoolean(
                        "success",
                        false
                    )

                if (!success) {

                    throw Exception(
                        json.optString(
                            "message",
                            "Recommendation request failed."
                        )
                    )
                }

                // =========================
                // PARSE PACKAGES
                // =========================

                val packagesArray =
                    json.optJSONArray("packages")
                        ?: JSONArray()

                val packages =
                    parsePackages(packagesArray)

                println(
                    "TABi PACKAGES FOUND: ${packages.size}"
                )

                callback(
                    Result.success(packages)
                )

            } catch (e: Exception) {

                e.printStackTrace()

                // IMPORTANT:
                // Return the REAL error instead
                // of hiding it.

                callback(
                    Result.failure(
                        Exception(
                            "TABi API Error:\n${e.message}",
                            e
                        )
                    )
                )

            } finally {

                connection?.disconnect()
            }
        }
    }

    // =========================
    // PARSE PACKAGES
    // =========================

    private fun parsePackages(
        array: JSONArray
    ): List<RemoteTripPackage> {

        val result =
            mutableListOf<RemoteTripPackage>()

        for (i in 0 until array.length()) {

            val item =
                array.getJSONObject(i)

            val highlightsJson =
                item.optJSONArray(
                    "highlights"
                ) ?: JSONArray()

            val highlights =
                mutableListOf<String>()

            for (j in 0 until highlightsJson.length()) {

                highlights.add(
                    highlightsJson.optString(j)
                )
            }

            result.add(
                RemoteTripPackage(

                    id = item.optString("id"),

                    title =
                        item.optString("title"),

                    route =
                        item.optString("route"),

                    days =
                        item.optInt("days"),

                    price =
                        item.optInt("price"),

                    rating =
                        item.optDouble("rating"),

                    style =
                        item.optString("style"),

                    highlights =
                        highlights
                )
            )
        }

        return result
    }
}

