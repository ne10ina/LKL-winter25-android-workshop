package ru.lkl.winter25.android.workshop

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.lkl.winter25.android.workshop.network.Joke
import ru.lkl.winter25.android.workshop.network.createJokeApiService

// Создаем интерфейс JokeState, который используется для описания текущего состояния загрузки шутки
sealed interface JokeState {
    // Состояние, когда шутка загружается
    data object Loading : JokeState

    // Состояние, когда шутка успешно загружена
    data class Success(val joke: Joke) : JokeState

    // Состояние ошибки при загрузке шутки
    data class Error(val message: String) : JokeState
}

// ViewModel для работы с шутками
class JokesViewModel : ViewModel() {

    // Закрытая переменная, которая содержит список всех полученных шуток
    private val _jokesList = MutableStateFlow<List<Joke>>(emptyList())

    // Закрытая переменная для хранения текущей шутки
    private val _currentJoke = MutableStateFlow<Joke>(
        Joke(
            error = true,
            category = "",
            type = "",
            setup = "",
            delivery = "",
            id = 0
        )
    )

    // Закрытая переменная для хранения текущего состояния загрузки шутки
    private val _currentJokeState = MutableStateFlow<JokeState>(JokeState.Loading)

    // Открытая переменная для наблюдения за состоянием загрузки шутки из интерфейса
    val currentJokeState: StateFlow<JokeState> = _currentJokeState

    // Ленивое создание сервиса для API шуток
    private val jokeApiService by lazy {
        createJokeApiService()
    }

    // Блок инициализации, который запускает загрузку первой шутки при создании ViewModel
    init {
        loadNextJoke()
    }

    // Функция для загрузки следующей шутки
    private fun loadNextJoke() {
        viewModelScope.launch {
            // Используем runCatching для обработки результата запроса
            runCatching {
                jokeApiService.getRandomProgrammingJoke() // Запрос новой шутки
            }.onSuccess { newJoke -> // Если запрос прошел успешно
                _jokesList.value += newJoke // Добавляем новую шутку в список
                _currentJoke.value = newJoke // Обновляем текущую шутку
                _currentJokeState.value =
                    JokeState.Success(newJoke) // Обновляем состояние на Success
            }.onFailure { e -> // Если произошла ошибка
                Log.e("JokesViewModel", "Error loading joke", e) // Логируем ошибку
                _currentJokeState.value =
                    JokeState.Error(e.message ?: "Unknown error") // Обновляем состояние на Error
            }
        }
    }

    // Функция для получения предыдущей шутки из списка
    fun previousJoke() {
        val currentIndex =
            _jokesList.value.indexOf(_currentJoke.value) // Получаем текущий индекс шутки
        if (currentIndex > 0) { // Если не текущая шутка не первая
            _currentJoke.value =
                _jokesList.value[currentIndex - 1] // Обновляем текущую шутку на предыдущую
            _currentJokeState.value =
                JokeState.Success(_currentJoke.value) // Обновляем состояние на Success
        }
    }

    // Функция для получения следующей шутки из списка
    fun nextJoke() {
        val currentIndex =
            _jokesList.value.indexOf(_currentJoke.value) // Получаем текущий индекс шутки
        if (currentIndex < _jokesList.value.size - 1) { // Если текущая не последняя в списке
            _currentJoke.value =
                _jokesList.value[currentIndex + 1] // Обновляем текущую шутку на следующую
            _currentJokeState.value =
                JokeState.Success(_currentJoke.value) // Обновляем состояние на Success
        } else if (currentIndex == _jokesList.value.size - 1) { // Если текущая последняя в списке
            loadNextJoke() // Загружаем новую шутку из API
        }
    }
}
