package ru.lkl.winter25.android.workshop.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET

// Определяем интерфейс JokeApi с функцией для получения случайной шутки
interface JokeApi {
    @GET("joke/programming?type=twopart") // Указываем HTTP метод GET и конец URL, чтобы получить программную шутку
    suspend fun getRandomProgrammingJoke(): Joke // Суспенс-функция для асинхронного получения объекта Joke
}

// Функция для создания экземпляра JokeApi
fun createJokeApiService(): JokeApi {
    // Базовый URL для API шуток
    val baseUrl = "https://v2.jokeapi.dev/"
    // Объект для настройки JSON парсера, который будет игнорировать неизвестные ключи
    val json = Json {
        ignoreUnknownKeys = true // Это полезно, если API возвращает неизвестные нам ключи
    }
    // Указываем тип содержимого, который будет поддерживать JSON
    val contentType = "application/json".toMediaType()

    // Создаем и настраиваем логгер для отображения HTTP запросов и ответов
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Уровень логирования - BODY, чтобы видеть тело запросов и ответов
    }

    // Создаем клиент OkHttp и добавляем к нему логгер
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging) // Добавляем логгер как интерсептор
        .build()

    // Создаем Retrofit-построитель для создания экземпляра JokeApi
    return Retrofit.Builder()
        .baseUrl(baseUrl) // Устанавливаем базовый URL
        .client(okHttpClient) // Используем сконфигурированный OkHttpClient
        .addConverterFactory(json.asConverterFactory(contentType)) // Добавляем фабрику конвертации для работы с JSON
        .build()
        .create(JokeApi::class.java) // Создаем экземпляр JokeApi
}
