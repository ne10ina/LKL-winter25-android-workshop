package ru.lkl.winter25.android.workshop.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET

interface JokeApi {
    @GET("joke/programming?type=twopart")
    suspend fun getRandomProgrammingJoke(): Joke
}

fun createJokeApiService(): JokeApi {
    val baseUrl = "https://v2.jokeapi.dev/"
    val json = Json {
        ignoreUnknownKeys = true
    }
    val contentType = "application/json".toMediaType()

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(JokeApi::class.java)
}
