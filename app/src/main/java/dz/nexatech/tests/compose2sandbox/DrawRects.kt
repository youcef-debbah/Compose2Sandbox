@file:OptIn(ExperimentalLayoutApi::class)

package dz.nexatech.tests.compose2sandbox

import android.graphics.drawable.Drawable
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
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlin.random.Random

const val RECT_COUNT = 1000
const val RANGE_PX = 32
const val WIDTH_PX = 32f

const val HALF_RECT_COUNT = RECT_COUNT.ushr(1)
const val HALF_RANG_PX = RANGE_PX.ushr(1)
const val HEIGHT_PX = WIDTH_PX + HALF_RANG_PX

val defaultBackground = Color.LightGray
val defaultForeground1 = Color.DarkGray
val defaultForeground2 = Color.Black
val defaultSizePx = Size(WIDTH_PX, WIDTH_PX)

@Composable
fun rememberAnimeCycle(): State<Float> {
    return rememberInfiniteTransition(label = "AnimationCycleTransition")
        .animateFloat(
            label = "AnimationCycle",
            initialValue = 0f,
            targetValue = 0.99999994f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
        )
}

fun calcPrecomputedViewGridOffsets(widthPx: Int): Pair<Array<Array<Offset>>, Array<Array<Offset>>> {
    val columns: Int = (widthPx / WIDTH_PX).toInt()
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
        for (r in 0 until rows) {
            for (c in 0 until columns) {
                if (c and 1 == r and 1) {
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
            if (c and 1 == rows and 1) {
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

    return Pair(allOffsets1, allOffsets2)
}

fun Int.revRange() = if (this > HALF_RANG_PX) RANGE_PX - this else this

@Composable
fun PrecomputedViewGrid(
    allOffsets: Pair<Array<Array<Offset>>, Array<Array<Offset>>>,
    background: Color = defaultBackground,
    foreground1: Color = defaultForeground1,
    foreground2: Color = defaultForeground2,
    foregroundSize: Size = defaultSizePx,
) {
    val cycle: State<Float> = rememberAnimeCycle()
    Spacer(
        Modifier
            .fillMaxSize()
            .drawWithContent {
                drawRect(color = background, Offset.Zero, size = size)

                val frameIndex = (cycle.value * RANGE_PX).toInt()
                val offsets1Px = allOffsets.first[frameIndex]
                val offsets2Px = allOffsets.second[frameIndex]

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
    )
}

fun calcSingleViewGridOffsets(): Array<FloatArray> {
    val offsetsPx: Array<FloatArray> = Array(RECT_COUNT) { _ ->
        val initOffset = Random.nextInt(0, RANGE_PX)
        FloatArray(RANGE_PX) { index ->
            val i = (index + initOffset) % RANGE_PX
            val d = if (i > HALF_RANG_PX) RANGE_PX - i else i
            d.toFloat()
        }
    }
    return offsetsPx
}

@Composable
fun SingleViewGrid(
    cycle: State<Float>,
    offsetsPx: Array<FloatArray>,
    background: Color = defaultBackground,
    foreground1: Color = defaultForeground1,
    foreground2: Color = defaultForeground2,
    widthPx: Float = WIDTH_PX,
    heightPx: Float = HEIGHT_PX,
    rectSizePx: Size = defaultSizePx,
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
                                color = if (c.and(1) == 0) foreground1 else foreground2,
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
                            color = if (c and 1 == 0) foreground1 else foreground2,
                            size = rectSizePx
                        )
                        x += widthPx
                    }
                }
            }
    )
}

fun calcFlowGridRectsOffsets() = Array(RECT_COUNT) { _ ->
    val initOffset = Random.nextInt(0, RANGE_PX)
    Array(RANGE_PX) { index ->
        val i = (index + initOffset) % RANGE_PX
        val d = if (i > HALF_RANG_PX) RANGE_PX - i else i
        Offset(0f, d.toFloat())
    }
}

@Composable
fun FlowGridRects(
    cycle: State<Float>,
    offsets: Array<Array<Offset>>,
    width: Dp,
    height: Dp,
    background: Color = defaultBackground,
    foreground1: Color = defaultForeground1,
    foreground2: Color = defaultForeground2,
    rectSizePx: Size = defaultSizePx,
) {
    FlowRow {
        DrawRectAnimationTraced(cycle, offsets, background, foreground1, width, height, rectSizePx)
        DrawRectAnimation(cycle, offsets, background, foreground1, width, height, rectSizePx, 1)
        for (id in 2 until RECT_COUNT step 2) {
            DrawRectAnimation(
                cycle,
                offsets,
                background,
                foreground1,
                width,
                height,
                rectSizePx,
                id
            )
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
fun DrawRectAnimationTraced(
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

@Composable
fun DrawRectAnimation(
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
fun DrawFramesAnimation(
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
fun ImageAnimation(drawable: Drawable) {
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
fun OffsetAnimation(
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