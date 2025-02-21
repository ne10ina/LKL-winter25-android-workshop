package ru.lkl.winter25.android.workshop

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.lkl.winter25.android.workshop.ui.theme.WorkshopTheme

// MainActivity - это главный экран приложения, который наследует от ComponentActivity
class MainActivity : ComponentActivity() {
    // Метод onCreate вызывается, когда активность создается
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Включает отображение приложения на весь экран, убирая системные отступы
        setContent {
            // Устанавливаем тему для приложения
            WorkshopTheme {
                // Scaffold создает базовую структуру для экрана
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Вызываем функцию Content, передавая ей отступы
                    Content(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Определяем @Composable функцию для создания контента приложения
@Composable
fun Content(modifier: Modifier) {
    // Создаем и получаем объект JokesViewModel
    val jokesViewModel: JokesViewModel = viewModel()
    // Собираем текущее состояние шутки из ViewModel как State
    val currentJokeState by jokesViewModel.currentJokeState.collectAsState()
    // Получаем текущий контекст из комбинаторного окружения
    val context = LocalContext.current

    // Создаем поверхность (Surface) с заполнением экрана и фоновым цветом темы
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Организуем элементы в колонку (вертикально)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Центрируем элементы по горизонтали
            verticalArrangement = Arrangement.SpaceBetween, // Распределяем элементы, оставляя пространство между ними
            modifier = Modifier.padding(16.dp) // Устанавливаем отступ по краям
        ) {
            // Верхняя панель приложения с заголовком
            JokeAppTopBar(context)
            // Контент шутки, Состояние текущей шутки
            JokeContent(currentJokeState)
            // Кнопки навигации для переключения шуток
            JokeNavigationButtons(jokesViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Функция для отображения верхней панели приложения с заголовком и кнопкой закрытия
fun JokeAppTopBar(context: Context) {
    TopAppBar(
        title = { Text("LKL Joke App") }, // Заголовок приложения
        actions = {
            // Кнопка для закрытия приложения
            TextButton(onClick = { (context as ComponentActivity).finish() }) {
                Text("Закрыть")
            }
        }
    )
}

@Composable
// Функция для отображения кнопок навигации "Назад" и "Вперед"
fun JokeNavigationButtons(jokesViewModel: JokesViewModel) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly, // Равномерное распределение кнопок
        modifier = Modifier.fillMaxWidth() // Занимаем всю ширину
    ) {
        // Кнопка "Назад", вызывает метод для показа предыдущей шутки
        Button(
            modifier = Modifier
                .weight(1f) // Каждая кнопка занимает одинаковое пространство
                .padding(end = 8.dp), // Отступ справа
            onClick = { jokesViewModel.previousJoke() }
        ) {
            Text("Назад")
        }
        // Кнопка "Вперед", вызывает метод для показа следующей шутки
        Button(
            modifier = Modifier
                .weight(1f) // Каждая кнопка занимает одинаковое пространство
                .padding(start = 8.dp), // Отступ слева
            onClick = { jokesViewModel.nextJoke() }
        ) {
            Text("Вперед")
        }
    }
}

@Composable
// Функция для отображения содержимого шутки в зависимости от ее состояния
fun ColumnScope.JokeContent(currentJokeState: JokeState) {
    when (currentJokeState) {
        is JokeState.Error -> JokeError(currentJokeState) // Отображаем ошибку, если есть
        is JokeState.Loading -> ShimmerLoadingAnimation() // Отображаем анимацию загрузки
        is JokeState.Success -> JokeDisplay(currentJokeState) // Отображаем шутку, если она загружена успешно
    }
}

@Composable
// Функция для отображения информации о шутке, когда она успешно загружена
fun ColumnScope.JokeDisplay(jokeState: JokeState.Success) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Центрируем элементы по горизонтали
        verticalArrangement = Arrangement.Center, // Центрируем элементы по вертикали
        modifier = Modifier.weight(1f) // Заполняем все доступное пространство
    ) {
        // Отображаем категорию шутки
        Text(
            modifier = Modifier.fillMaxWidth(), // Устанавливаем ширину текста на максимально возможное значение
            text = "Категория: ${jokeState.joke.category}",
            style = MaterialTheme.typography.titleMedium, // Используем стиль заголовка среднего размера
            textAlign = TextAlign.End // Выравниваем текст по правому краю
        )
        Spacer(modifier = Modifier.height(8.dp)) // Отступ между текстами
        // Отображаем начальную фразу шутки
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = jokeState.joke.setup,
            style = MaterialTheme.typography.titleLarge, // Используем стиль заголовка
            textAlign = TextAlign.Start // Выравниваем текст по левому краю
        )
        Spacer(modifier = Modifier.height(8.dp)) // Отступ между текстами
        // Отображаем ответную фразу шутки
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = jokeState.joke.delivery,
            style = MaterialTheme.typography.titleMedium, // Используем стиль заголовка среднего размера
            textAlign = TextAlign.Start // Выравниваем текст по левому краю
        )
    }
}

// Функция для отображения сообщения об ошибке
@Composable
fun JokeError(jokeState: JokeState.Error) {
    // Отображаем текст с ошибкой, используя стили Material Design
    Text(
        text = "Что-то пошло не так: ${jokeState.message}", // Сообщение об ошибке
        color = MaterialTheme.colorScheme.error, // Цвет ошибки из темы
        textAlign = TextAlign.Center, // Центрируем текст по горизонтали
        style = MaterialTheme.typography.bodyLarge, // Стиль текста
        modifier = Modifier.fillMaxWidth() // Текст занимает всю ширину
    )
}

// Функция для отображения анимации загрузки в виде "шиммера"
@Composable
fun ShimmerLoadingAnimation() {
    // Определяем цвета для анимации "шиммера"
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surface, // Цвет поверхности
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), // Светлая версия цвета поверхности
        MaterialTheme.colorScheme.surface // Цвет поверхности
    )

    // Создаем бесконечную анимацию
    val transition = rememberInfiniteTransition()
    val xShimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                1000,
                easing = LinearEasing
            ), // Анимация, идущая линейно в течение одной секунды
            repeatMode = RepeatMode.Restart // Перезапуск анимации после завершения
        )
    )

    // Используем линейный градиент для создания эффекта "шиммера"
    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(xShimmer, 0f), // Начальная точка градиента
        end = Offset(xShimmer + 200f, 0f) // Конечная точка градиента
    )

    // Отображаем столбец с "шиммером"
    Column {
        // Создаем несколько строк с "шиммингом"
        listOf(20.dp to 0.5f, 30.dp to 1f, 20.dp to 1f).forEach { (height, width) ->
            Spacer(
                modifier = Modifier
                    .height(height) // Устанавливаем высоту строки
                    .fillMaxWidth(width) // Устанавливаем ширину строки
                    .background(shimmerBrush) // Заливаем фоном с "шиммером"
            )
            Spacer(modifier = Modifier.height(8.dp)) // Отступ между строками
        }
    }
}

// Функция предпросмотра интерфейса, отмеченная аннотацией @Preview
@Preview
@Composable
private fun ContentPreview() {
    WorkshopTheme {
        Content(Modifier) // Вызываем функцию Content для отображения ее при предпросмотре
    }
}
