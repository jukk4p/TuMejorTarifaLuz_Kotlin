package com.tumejortarifaluz.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

@Composable
fun LottieLoadingView(
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever
) {
    // In a real app, you would put your lottie json in res/raw/loading.json
    // For this prototype, if the resource is missing, we could show a fallback
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(0 /* R.raw.loading */))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Fallback to standard indicator if JSON is missing
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
