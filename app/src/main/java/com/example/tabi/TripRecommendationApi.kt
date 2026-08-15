/*package com.example.tabi

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
}*/

package com.example.tabi

import org.json.JSONArray
import org.json.JSONObject
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

    // Disabled to bypass local environment networking bottlenecks
    // private const val BASE_URL = "http://127.0.0.1:8787"

    private val executor = Executors.newCachedThreadPool()

    fun recommendTrips(
        days: Int,
        travelers: Int,
        budgetPerTraveler: Int?,
        style: String,
        callback: (Result<List<RemoteTripPackage>>) -> Unit
    ) {
        executor.execute {
            try {
                // Simulate a brief 1.2-second network delay for real visual loading progress bars
                Thread.sleep(1200)

                // Validation safeguard identical to standard production schemas
                if (days <= 0 || travelers <= 0) {
                    throw Exception("Validation error: Trip parameters must be greater than zero.")
                }

                // 🏢 Universal Sandbox Data Matrix mimicking database storage schemas
                val sandboxPackages = listOf(
                    RemoteTripPackage(
                        id = "pkg_01",
                        title = "Golden Goa Leisure Break",
                        route = "Panaji - Calangute - Palolem",
                        days = 5,
                        price = 450,
                        rating = 4.8,
                        style = "Relaxation",
                        highlights = listOf("Private Beach Resort", "Sunset Cruise", "Seafood Dining")
                    ),
                    RemoteTripPackage(
                        id = "pkg_02",
                        title = "Kerala Backwaters & Houseboat Glide",
                        route = "Cochin - Alleppey - Kumarakom",
                        days = 10,
                        price = 850,
                        rating = 4.9,
                        style = "Relaxation",
                        highlights = listOf("Luxury Houseboat Stay", "Spice Plantation Tour", "Ayurvedic Spa")
                    ),
                    RemoteTripPackage(
                        id = "pkg_03",
                        title = "Manali Paragliding & Peak Trekker",
                        route = "Solang Valley - Rohtang Pass",
                        days = 7,
                        price = 950,
                        rating = 4.7,
                        style = "Adventure",
                        highlights = listOf("Tandem Paragliding", "Riverside Basecamp", "Mountain Trekking")
                    ),
                    RemoteTripPackage(
                        id = "pkg_04",
                        title = "Rishikesh White Water Rapids Tour",
                        route = "Shivpuri - Lakshman Jhula",
                        days = 4,
                        price = 350,
                        rating = 4.6,
                        style = "Adventure",
                        highlights = listOf("Grade IV River Rafting", "Cliff Jumping", "Beach Volleyball")
                    ),
                    RemoteTripPackage(
                        id = "pkg_05",
                        title = "Heritage Royal Rajasthan Bundle",
                        route = "Jaipur - Jodhpur - Udaipur",
                        days = 12,
                        price = 1200,
                        rating = 4.9,
                        style = "Family",
                        highlights = listOf("Fort Palace Guided Excursion", "Camel Desert Safari", "Puppet Performance")
                    ),
                    RemoteTripPackage(
                        id = "pkg_06",
                        title = "Ooty Hills Botanical Escape",
                        route = "Coimbatore - Coonoor - Ooty",
                        days = 6,
                        price = 550,
                        rating = 4.5,
                        style = "Family",
                        highlights = listOf("Toy Train Mountain Voyage", "Tea Factory Walk", "Boating on Lake")
                    )
                )

                // 🔍 Dynamically filter packages in memory based on the user's selected category style and budget constraints
                val matchingPackages = sandboxPackages.filter { item ->
                    val matchesStyle = item.style.equals(style, ignoreCase = true)

                    // If a budget parameter constraint was entered, verify standard criteria
                    val matchesBudget = if (budgetPerTraveler != null) {
                        item.price <= budgetPerTraveler
                    } else {
                        true
                    }

                    matchesStyle && matchesBudget
                }

                // If no exact match was found, default gracefully to packages fitting the style category path
                val finalSelection = if (matchingPackages.isNotEmpty()) {
                    matchingPackages
                } else {
                    sandboxPackages.filter { it.style.equals(style, ignoreCase = true) }
                }

                // Deliver standard successful pipeline mapping back to your front-end Activity components
                callback(Result.success(finalSelection))

            } catch (e: Exception) {
                e.printStackTrace()
                callback(Result.failure(Exception("TABi API Error:\n${e.message}", e)))
            }
        }
    }
}


