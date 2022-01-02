package com.jetpack.customvviewpulltorefresh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jetpack.customvviewpulltorefresh.ui.theme.CustomViewPullToRefreshTheme
import com.jetpack.customvviewpulltorefresh.ui.theme.RedYellow
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomViewPullToRefreshTheme {
                Surface(color = MaterialTheme.colors.background) {
                    CustomViewPullToRefresh()
                }
            }
        }
    }
}

@Composable
fun CustomViewPullToRefresh() {
    val items = remember { mutableStateListOf<Int>() }
    items.addAll(0..10)
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Custom View Pull To Refresh",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                }
            )
        }
    ) {
        Column {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    isRefreshing = true
                },
                indicator = { state, trigger ->
                    CustomViewPullRefreshView(state, trigger)
                }
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items.size) {
                        ColumnRefreshItem(items[it])
                    }
                }
                LaunchedEffect(isRefreshing) {
                    if (isRefreshing) {
                        delay(1000L)
                        items.clear()
                        items.addAll((1..10).map {
                            (0..100).random()
                        })
                        isRefreshing = false
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnRefreshItem(number: Int) {
    Card(
        modifier = Modifier.wrapContentSize(),
        elevation = 10.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text(
                text = "Item Number $number",
                color = Color.Black,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

@Composable
fun CustomViewPullRefreshView(
    swipeRefreshState: SwipeRefreshState,
    refreshTriggerDistance: Dp,
    color: Color = MaterialTheme.colors.primary
) {
    Box(
        Modifier
            .drawWithCache {
                onDrawBehind {
                    val distance = refreshTriggerDistance.toPx()
                    val progress = (swipeRefreshState.indicatorOffset / distance).coerceIn(0f, 1f)
                    val brush = Brush.verticalGradient(
                        0f to color.copy(alpha = 0.45f),
                        1f to color.copy(alpha = 0f)
                    )
                    drawRect(
                        brush = brush,
                        alpha = FastOutSlowInEasing.transform(progress)
                    )
                }
            }
            .fillMaxWidth()
            .height(80.dp)
    ) {
        if (swipeRefreshState.isRefreshing) {
            LinearProgressIndicator(
                Modifier.fillMaxWidth(),
                color = RedYellow
            )
        } else {
            val trigger = with(LocalDensity.current) { refreshTriggerDistance.toPx() }
            val progress = (swipeRefreshState.indicatorOffset / trigger).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}















