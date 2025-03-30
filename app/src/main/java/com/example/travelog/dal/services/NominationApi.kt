package com.example.travelog.dal.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder





object NominationApi {


    /**
     * Calls the Nominatim API with the given [query] (which can be a partial name)
     * and returns a list of matching city display names.
     *
     * Extra URL parameters:
     * - format=json: return results as JSON.
     * - addressdetails=1: include address details.
     * - limit=10: return at most 10 results.
     * - accept-language=en: return results in English (adjust as needed).
     *
     * This function must be called from within a coroutine.
     */
    suspend fun autoCompleteName(query: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                // URL-encode the query string
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                // Build the URL with extra parameters to support partial matching.
                // You can call this function with a partial name like "Lon" and get suggestions.
                val urlString = "https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&addressdetails=1&limit=10&accept-language=en"
                val url = URL(urlString)

                // Open a connection and set request properties.
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                // Nominatim requires a valid User-Agent header.
                connection.setRequestProperty("User-Agent", "TravelogApp/1.0 (contact@example.com)")
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the entire response.
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(responseText)
                    val names = mutableListOf<String>()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        names.add(obj.getString("display_name"))
                    }
                    names
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }



}