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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.lkl.winter25.android.workshop.ui.theme.WorkshopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkshopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Content(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Content(modifier: Modifier) {
    val jokesViewModel: JokesViewModel = viewModel()
    val currentJokeState by jokesViewModel.currentJokeState.collectAsState()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(16.dp)
        ) {
            JokeAppTopBar(context)
            JokeContent(currentJokeState)
            JokeNavigationButtons(jokesViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JokeAppTopBar(context: Context) {
    TopAppBar(
        title = { Text("LKL Joke App") },
        actions = {
            TextButton(onClick = { (context as ComponentActivity).finish() }) {
                Text("Закрыть")
            }
        }
    )
}

@Composable
fun JokeNavigationButtons(jokesViewModel: JokesViewModel) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            onClick = { jokesViewModel.previousJoke() }
        ) {
            Text("Назад")
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            onClick = { jokesViewModel.nextJoke() }
        ) {
            Text("Вперед")
        }
    }
}

@Composable
fun ColumnScope.JokeContent(currentJokeState: JokeState) {
    when (currentJokeState) {
        is JokeState.Error -> JokeError(currentJokeState)
        is JokeState.Loading -> ShimmerLoadingAnimation()
        is JokeState.Success -> JokeDisplay(currentJokeState)
    }
}

@Composable
fun ColumnScope.JokeDisplay(jokeState: JokeState.Success) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Категория: ${jokeState.joke.category}",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = jokeState.joke.setup,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = jokeState.joke.delivery,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun JokeError(jokeState: JokeState.Error) {
    Text(
        text = "Что-то пошло не так: ${jokeState.message}",
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ShimmerLoadingAnimation() {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surface
    )

    val transition = rememberInfiniteTransition()
    val xShimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(xShimmer, 0f),
        end = Offset(xShimmer + 200f, 0f)
    )

    Column {
        listOf(20.dp to 0.5f, 30.dp to 1f, 20.dp to 1f).forEach { (height, width) ->
            Spacer(
                modifier = Modifier
                    .height(height)
                    .fillMaxWidth(width)
                    .background(shimmerBrush)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
