package com.example.apiretask.ui.presentation.cats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.apiretask.data.CatImageDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatScreen(
    viewModel: CatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Cat Gallery") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("loading_indicator")
                    )
                }
                state.error != null && state.catImages.isEmpty() -> {
                    ErrorView(
                        errorMessage = state.error!!,
                        onRetry = { viewModel.processIntent(CatIntent.RefreshCatImages) }
                    )
                }
                state.catImages.isEmpty() -> {
                    EmptyView(
                        onRefresh = { viewModel.processIntent(CatIntent.RefreshCatImages) }
                    )
                }
                else -> {
                    CatImagesContent(
                        catImages = state.catImages,
                        onRefresh = { viewModel.processIntent(CatIntent.RefreshCatImages) },
                        errorMessage = state.error
                    )
                }
            }
        }
    }
}

@Composable
fun CatImagesContent(
    catImages: List<CatImageDto>,
    onRefresh: () -> Unit,
    errorMessage: String? = null
) {
    Column(modifier = Modifier.fillMaxSize()) {
        errorMessage?.let {
            Text(
                text = "Error: $it",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Button(
            onClick = onRefresh,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
                .testTag("refresh_button")
        ) {
            Text(text = "Refresh Images")
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.testTag("cat_list")
        ) {
            items(catImages) { catImage ->
                CatImageItem(catImage = catImage)
            }
        }
    }
}

@Composable
fun CatImageItem(catImage: CatImageDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        AsyncImage(
            model = catImage.url,
            contentDescription = "Cat image ${catImage.id}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ErrorView(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $errorMessage",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.testTag("retry_button")
        ) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun EmptyView(
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No cat images found",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRefresh,
            modifier = Modifier.testTag("empty_refresh_button")
        ) {
            Text(text = "Refresh")
        }
    }
}