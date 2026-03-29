package edu.nd.pmcburne.hello.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import kotlinx.serialization.json.Json
import okhttp3.Request

class LocationRepository {
    private val client = OkHttpClient()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun fetchLocations(url: String): List<Location> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Network request failed: ${response.code}")
            val body = response.body?.string() ?: throw Exception("Empty response body")
            return@withContext json.decodeFromString<List<Location>>(body)
        }
    }
}