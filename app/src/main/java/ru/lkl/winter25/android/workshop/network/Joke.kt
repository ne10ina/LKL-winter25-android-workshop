package ru.lkl.winter25.android.workshop.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Объявляем класс данных Joke, который отвечает за хранение информации о шутке
@Serializable // Аннотация, указывающая, что этот класс может быть сериализован (преобразован в JSON и обратно)
data class Joke(
    @SerialName("error") val error: Boolean, // Поле для обозначения ошибок (если true, значит произошла ошибка)
    @SerialName("category") val category: String, // Категория шутки (например, "Programming")
    @SerialName("type") val type: String, // Тип шутки (например, "twopart" для шуток с двумя частями)
    @SerialName("setup") val setup: String, // Начальная часть шутки
    @SerialName("delivery") val delivery: String, // Заключительная часть шутки
    @SerialName("id") val id: Int // Уникальный идентификатор шутки
)
