package ru.lkl.winter25.android.workshop

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.lkl.winter25.android.workshop.network.Joke
import ru.lkl.winter25.android.workshop.network.createJokeApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface JokeState {
    data object Loading : JokeState
    data class Success(val joke: Joke) : JokeState
    data class Error(val message: String) : JokeState
}

class JokesViewModel : ViewModel() {
    private val _jokesList = MutableStateFlow<List<Joke>>(emptyList())
    private val _currentJoke = MutableStateFlow<Joke>(Joke(error = true, category = "", type = "", setup = "", delivery = "", id = 0))

    private val _currentJokeState = MutableStateFlow<JokeState>(JokeState.Loading)
    val currentJokeState: StateFlow<JokeState> = _currentJokeState

    private val jokeApiService by lazy {
        createJokeApiService()
    }

    init {
        loadNextJoke()
    }

    private fun loadNextJoke() {
        viewModelScope.launch {
            runCatching {
                jokeApiService.getRandomProgrammingJoke()
            }.onSuccess { newJoke ->
                _jokesList.value += newJoke
                _currentJoke.value = newJoke
                _currentJokeState.value = JokeState.Success(newJoke)
            }.onFailure { e ->
                Log.e("JokesViewModel", "Error loading joke", e)
                _currentJokeState.value = JokeState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun previousJoke() {
        val currentIndex = _jokesList.value.indexOf(_currentJoke.value)
        if (currentIndex > 0) {
            _currentJoke.value = _jokesList.value[currentIndex - 1]
            _currentJokeState.value = JokeState.Success(_currentJoke.value)
        }
    }

    fun nextJoke() {
        val currentIndex = _jokesList.value.indexOf(_currentJoke.value)
        if (currentIndex < _jokesList.value.size - 1) {
            _currentJoke.value = _jokesList.value[currentIndex + 1]
            _currentJokeState.value = JokeState.Success(_currentJoke.value)
        } else if (currentIndex == _jokesList.value.size - 1) {
            loadNextJoke()
        }
    }
}
