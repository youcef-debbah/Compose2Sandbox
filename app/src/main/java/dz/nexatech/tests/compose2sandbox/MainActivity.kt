@file:OptIn(ExperimentalLayoutApi::class)

package dz.nexatech.tests.compose2sandbox

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

const val rangePx = 16
const val halfRangePx = rangePx.ushr(1)
const val rectCount = 6
const val widthPx = 300f
const val heightPx = widthPx + halfRangePx

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val res = resources
        val density: Float = res.displayMetrics.density

        setContent {
            Text("Loading...")
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val background = Color.Blue
//            val foreground = Color(0xFFED1C24)
            val foreground = Color.Red
            val foreground2 = Color.Green

            val width = Dp(widthPx / density)
            val height = Dp(heightPx / density)
            val size = Size(width.value, width.value)
            val sizePx = Size(widthPx, widthPx)

            val offsets: Array<Array<Offset>> = Array(rectCount) { _ ->
                val initOffset = Random.nextInt(0, rangePx)
                Array(rangePx) { index ->
                    val i = (index + initOffset) % rangePx
                    val d = if (i > halfRangePx) rangePx - i else i
                    Offset(0f, d.toFloat())
                }
            }

            val offsetsPx: IntArray = IntArray(rectCount) {
                Random.nextInt(0, rangePx)
            }

            withContext(Dispatchers.Main) {
                reportFullyDrawn()
                setContent {
                    val anime: InfiniteTransition =
                        rememberInfiniteTransition(label = "DrawAnimation")
                    val cycle: State<Float> = anime.animateFloat(
                        label = "animeCycle",
                        initialValue = 0f,
                        targetValue = 0.99999994f,
                        animationSpec = infiniteRepeatable<Float>(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                    )

                    AnimatedGrid(
                        rectCount = rectCount,
                        cycle = cycle,
                        offsetsPx = offsetsPx,
                        background = background,
                        foreground = foreground,
                        widthPx = widthPx,
                        heightPx = heightPx,
                        rectSizePx = sizePx,
                        foreground2 = foreground2
                    )

//                    Row(
//                        horizontalArrangement = Arrangement.Absolute.Center,
//                        verticalAlignment = Alignment.Top,
//                        modifier = Modifier
//                            .padding(40.dp)
//                            .fillMaxSize()
//                    ) {
//                        Spacer(Modifier.background(Color.Green).size(64.dp))
//                        DrawRectAnimation(cycle, offsets, background, width, height)
//                        DrawFramesAnimation(cycle, frames, background, width, height)
//                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        println("FPS: " + fpsHistory.copyOf(fpsHistory.let {
            for (i in fpsHistory.size - 1 downTo 0) {
                if (fpsHistory[i] != 0) return@let i + 1
            }
            fpsHistory.size
        }).contentToString())
    }
}

@Composable
private fun AnimatedGrid(
    rectCount: Int,
    cycle: State<Float>,
    offsetsPx: IntArray,
    background: Color,
    foreground: Color,
    widthPx: Float,
    heightPx: Float,
    rectSizePx: Size,
    foreground2: Color,
) {
    Spacer(
        Modifier
            .fillMaxSize()
            .drawWithCache {
                val columns: Int = (size.width / widthPx).toInt()
                val rows: Int = rectCount / columns
                onDrawWithContent {
                    drawRect(color = background, Offset.Zero, size = size)
                    var x = 0f
                    var y = cycle.value
                    var i = 0
                    for (r in 0 until rows) {
                        for (c in 0 until columns step 2) {
                            drawRect(
                                topLeft = Offset(x, y + offsetsPx[i++]),
                                color = foreground,
                                size = rectSizePx
                            )
                            x += widthPx
                            drawRect(
                                topLeft = Offset(x, y + offsetsPx[i++]),
                                color = foreground2,
                                size = rectSizePx
                            )
                            x += widthPx
                        }
                        y += heightPx
                        x = 0f
                    }

                    for (c in 0 until rectCount % columns step 2) {
                        drawRect(
                            topLeft = Offset(x, y + offsetsPx[i++]),
                            color = foreground,
                            size = rectSizePx
                        )
                        x += widthPx
                        drawRect(
                            topLeft = Offset(x, y + offsetsPx[i++]),
                            color = foreground2,
                            size = rectSizePx
                        )
                        x += widthPx
                    }
                }
            }
    )
}

@Composable
private fun RectGrid(
    rectCount: Int,
    cycle: State<Float>,
    offsets: Array<Array<Offset>>,
    background: Color,
    foreground: Color,
    width: Dp,
    height: Dp,
    RectSizePx: Size,
    foreground2: Color,
) {
    FlowRow {
        DrawRectAnimationTraced(cycle, offsets, background, foreground, width, height, RectSizePx)
        DrawRectAnimation(cycle, offsets, background, foreground, width, height, RectSizePx, 1)
        for (id in 2 until rectCount step 2) {
            DrawRectAnimation(cycle, offsets, background, foreground, width, height, RectSizePx, id)
            DrawRectAnimation(
                cycle,
                offsets,
                background,
                foreground2,
                width,
                height,
                RectSizePx,
                id + 1
            )
        }
    }
}

@Composable
private fun DrawRectAnimationTraced(
    index: State<Float>,
    offsets: Array<Array<Offset>>,
    background: Color,
    foreground: Color,
    width: Dp,
    height: Dp,
    RectSizePx: Size,
) {
    Spacer(
        Modifier
            .width(width)
            .height(height)
            .background(background)
            .drawWithContent {
//                val frameIndex = System.currentTimeMillis().minus(t0).toInt() / 1000
//                fpsHistory[frameIndex]++
//                if (fpsHistory[frameIndex - 1] != currFps) {
//                    currFps = fpsHistory[frameIndex - 1]
//                    println("current FPS: $currFps")
//                }
                drawRect(
                    color = foreground,
                    topLeft = offsets[0][index.value
                        .times(rangePx)
                        .toInt()],
                    size = RectSizePx
                )
            }
    )
}

val t0 = System.currentTimeMillis() - 1000L
var fpsHistory = IntArray(3600)
var currFps = 0

@Composable
private fun DrawRectAnimation(
    index: State<Float>,
    offsets: Array<Array<Offset>>,
    background: Color,
    foreground: Color,
    width: Dp,
    height: Dp,
    RectSizePx: Size,
    id: Int,
) {
    Spacer(
        Modifier
            .width(width)
            .height(height)
            .background(background)
            .drawWithContent {
                drawRect(
                    color = foreground,
                    topLeft = offsets[id][index.value
                        .times(rangePx)
                        .toInt()],
                    size = RectSizePx
                )
            }
    )
}

@Composable
private fun DrawFramesAnimation(
    index: State<Float>,
    frames: Array<ImageBitmap>,
    background: Color,
    width: Dp,
    height: Dp
) {
    Spacer(
        Modifier
            .width(width)
            .height(height)
            .background(background)
            .drawWithContent {
                drawImage(
                    frames[index.value
                        .times(rangePx)
                        .toInt()]
                )
            }
    )
}

@Composable
private fun ImageAnimation(drawable: Drawable) {
    val painter = rememberDrawablePainter(drawable)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .width(100.dp)
            .height(132.dp),
        contentScale = ContentScale.FillBounds,
    )
}

@Composable
private fun OffsetAnimation(
    index: State<Float>,
    offsets: Array<IntOffset>,
    background: Color,
    foreground: Color,
    width: Dp,
    height: Dp
) {
    Box(
        Modifier
            .width(width)
            .height(height)
            .background(background)
    ) {
        Box(
            Modifier
                .offset { offsets[index.value.toInt()] }
                .size(width)
                .background(foreground)
        )
    }
}