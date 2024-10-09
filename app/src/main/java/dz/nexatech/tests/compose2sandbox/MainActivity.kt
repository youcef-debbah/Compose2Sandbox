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

const val RECT_COUNT = 1000
const val RANGE_PX = 16
const val WIDTH_PX = 20f

const val HALF_RECT_COUNT = RECT_COUNT.ushr(1)
const val HALF_RANG_PX = RANGE_PX.ushr(1)
const val HEIGHT_PX = WIDTH_PX + HALF_RANG_PX

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
            val foreground1 = Color.Red
            val foreground2 = Color.Green

            val width = Dp(WIDTH_PX / density)
            val height = Dp(HEIGHT_PX / density)
            val sizePx = Size(WIDTH_PX, WIDTH_PX)

//            val offsetsPx: Array<FloatArray> = Array(rectCount) { _ ->
//                val initOffset = Random.nextInt(0, rangePx)
//                FloatArray(rangePx) { index ->
//                    val i = (index + initOffset) % rangePx
//                    val d = if (i > halfRangePx) rangePx - i else i
//                    d.toFloat()
//                }
//            }

//            val offsets: Array<Array<Offset>> = Array(rectCount) { _ ->
//                val initOffset = Random.nextInt(0, rangePx)
//                Array(rangePx) { index ->
//                    val i = (index + initOffset) % rangePx
//                    val d = if (i > halfRangePx) rangePx - i else i
//                    Offset(0f, d.toFloat())
//                }
//            }

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

                    PrecomputedViewGrid(
                        cycle = cycle,
                        background = background,
                        foreground1 = foreground1,
                        foreground2 = foreground2,
                        foregroundSize = sizePx
                    )

//                    SingleViewGrid(
//                        cycle = cycle,
//                        offsetsPx = offsetsPx,
//                        background = background,
//                        foreground = foreground1,
//                        widthPx = widthPx,
//                        heightPx = heightPx,
//                        rectSizePx = sizePx,
//                        foreground2 = foreground2
//                    )

//                    FlowGridRects(
//                        cycle = cycle,
//                        offsets = offsets,
//                        background = background,
//                        foreground = foreground,
//                        width = width,
//                        height = height,
//                        rectSizePx = sizePx,
//                        foreground2 = foreground2
//                    )

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
private fun PrecomputedViewGrid(
    cycle: State<Float>,
    background: Color,
    foreground1: Color,
    foreground2: Color,
    foregroundSize: Size,
) {
    Spacer(
        Modifier
            .fillMaxSize()
            .drawWithCache {
                val columns: Int = (size.width / WIDTH_PX).toInt()
                val rows: Int = RECT_COUNT / columns

                val starts1 = IntArray(RECT_COUNT) { Random.nextInt(RANGE_PX) }
                val starts2 = IntArray(RECT_COUNT) { Random.nextInt(RANGE_PX) }

                val allOffsets1: Array<Array<Offset>> = Array(RANGE_PX) { _ ->
                    Array(RECT_COUNT - HALF_RECT_COUNT) { _ -> Offset.Zero }
                }

                val allOffsets2: Array<Array<Offset>> = Array(RANGE_PX) { _ ->
                    Array(HALF_RECT_COUNT) { _ -> Offset.Zero }
                }

                for (frameIndex in 0 until RANGE_PX) {
                    var x = 0f
                    var y = 0f
                    var i1 = 0
                    var i2 = 0
                    repeat(rows) {
                        for (c in 0 until columns) {
                            if (c and 1 == 0) {
                                allOffsets1[frameIndex][i1] =
                                    Offset(x, y + starts1[i1].plus(frameIndex).mod(RANGE_PX).revRange())
                                i1++
                            } else {
                                allOffsets2[frameIndex][i2] =
                                    Offset(x, y + starts2[i2].plus(frameIndex).mod(RANGE_PX).revRange())
                                i2++
                            }
                            x += WIDTH_PX
                        }
                        y += HEIGHT_PX
                        x = 0f
                    }

                    for (c in 0 until RECT_COUNT % columns) {
                        if (c and 1 == 0) {
                            allOffsets1[frameIndex][i1] =
                                Offset(x, y + starts1[i1].plus(frameIndex).mod(RANGE_PX).revRange())
                            i1++
                        } else {
                            allOffsets2[frameIndex][i2] =
                                Offset(x, y + starts2[i2].plus(frameIndex).mod(RANGE_PX).revRange())
                            i2++
                        }
                        x += WIDTH_PX
                    }
                }

                onDrawWithContent {
                    drawRect(color = background, Offset.Zero, size = size)

                    val frameIndex = (cycle.value * RANGE_PX).toInt()
                    val offsets1Px = allOffsets1[frameIndex]
                    val offsets2Px = allOffsets2[frameIndex]

                    for (offset in offsets1Px) {
                        drawRect(
                            color = foreground1,
                            topLeft = offset,
                            size = foregroundSize
                        )
                    }

                    for (offset in offsets2Px) {
                        drawRect(
                            color = foreground2,
                            topLeft = offset,
                            size = foregroundSize
                        )
                    }
                }
            }
    )
}

private fun Int.revRange() = if (this > HALF_RANG_PX) RANGE_PX - this else this

@Composable
private fun SingleViewGrid(
    cycle: State<Float>,
    offsetsPx: Array<FloatArray>,
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
                val rows: Int = RECT_COUNT / columns
                onDrawWithContent {
                    drawRect(color = background, Offset.Zero, size = size)
                    var x = 0f
                    var y = 0f
                    var id = 0
                    val frameIndex = cycle.value
                        .times(RANGE_PX)
                        .toInt()
                    repeat(rows) {
                        for (c in 0 until columns) {
                            drawRect(
                                topLeft = Offset(x, y + offsetsPx[id++][frameIndex]),
                                color = if (c.and(1) == 0) foreground else foreground2,
                                size = rectSizePx
                            )
                            x += widthPx
                        }
                        y += heightPx
                        x = 0f
                    }

                    for (c in 0 until RECT_COUNT % columns) {
                        drawRect(
                            topLeft = Offset(x, y + offsetsPx[id++][frameIndex]),
                            color = if (c and 1 == 0) foreground else foreground2,
                            size = rectSizePx
                        )
                        x += widthPx
                    }
                }
            }
    )
}

@Composable
private fun FlowGridRects(
    cycle: State<Float>,
    offsets: Array<Array<Offset>>,
    background: Color,
    foreground: Color,
    width: Dp,
    height: Dp,
    rectSizePx: Size,
    foreground2: Color,
) {
    FlowRow {
        DrawRectAnimationTraced(cycle, offsets, background, foreground, width, height, rectSizePx)
        DrawRectAnimation(cycle, offsets, background, foreground, width, height, rectSizePx, 1)
        for (id in 2 until RECT_COUNT step 2) {
            DrawRectAnimation(cycle, offsets, background, foreground, width, height, rectSizePx, id)
            DrawRectAnimation(
                cycle,
                offsets,
                background,
                foreground2,
                width,
                height,
                rectSizePx,
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
                        .times(RANGE_PX)
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
                        .times(RANGE_PX)
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
                        .times(RANGE_PX)
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