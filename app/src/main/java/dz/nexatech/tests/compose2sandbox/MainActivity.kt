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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val framesRes: Array<Int> = arrayOf(
    R.drawable.i00,
    R.drawable.i01,
    R.drawable.i02,
    R.drawable.i03,
    R.drawable.i04,
    R.drawable.i05,
    R.drawable.i06,
    R.drawable.i07,
    R.drawable.i08,
    R.drawable.i09,

    R.drawable.i10,
    R.drawable.i11,
    R.drawable.i12,
    R.drawable.i13,
    R.drawable.i14,
    R.drawable.i15,
    R.drawable.i16,
    R.drawable.i17,
    R.drawable.i18,
    R.drawable.i19,

    R.drawable.i20,
    R.drawable.i21,
    R.drawable.i22,
    R.drawable.i23,
    R.drawable.i24,
    R.drawable.i25,
    R.drawable.i26,
    R.drawable.i27,
    R.drawable.i28,
    R.drawable.i29,

    R.drawable.i30,
    R.drawable.i31,
    R.drawable.i32,
    R.drawable.i33,
    R.drawable.i34,
    R.drawable.i35,
    R.drawable.i36,
    R.drawable.i37,
    R.drawable.i38,
    R.drawable.i39,

    R.drawable.i40,
    R.drawable.i41,
    R.drawable.i42,
    R.drawable.i43,
    R.drawable.i44,
    R.drawable.i45,
    R.drawable.i46,
    R.drawable.i47,
    R.drawable.i48,
    R.drawable.i49,

    R.drawable.i50,
    R.drawable.i51,
    R.drawable.i52,
    R.drawable.i53,
    R.drawable.i54,
    R.drawable.i55,
    R.drawable.i56,
    R.drawable.i57,
    R.drawable.i58,
    R.drawable.i59,

    R.drawable.i60,
    R.drawable.i61,
    R.drawable.i62,
    R.drawable.i63,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val res = resources
        val density: Float = res.displayMetrics.density

        setContent {
            Text("Loading...")
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val frames = Array(framesRes.size) {
                ImageBitmap.imageResource(res, framesRes[it])
            }

            val offsets: Array<Offset> = Array(64) { index ->
                val d = if (index > 32) 64 - index else index
                Offset(0f, d * density)
            }

            val background = Color.Blue
//            val foreground = Color(0xFFED1C24)
            val foreground = Color.Red

            val width = 100.dp
            val height = 150.dp

            withContext(Dispatchers.Main) {
                setContent {
                    val anime: InfiniteTransition =
                        rememberInfiniteTransition(label = "DrawAnimation")
                    val cycle: State<Float> = anime.animateFloat(
                        label = "animeCycle",
                        initialValue = 0f,
                        targetValue = 63.9999f,
                        animationSpec = infiniteRepeatable<Float>(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                    )
                    Row(
                        horizontalArrangement = Arrangement.Absolute.Center,
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .padding(40.dp)
                            .fillMaxSize()
                    ) {
//                        Spacer(Modifier.background(Color.Green).size(64.dp))
                        DrawRectAnimation(cycle, offsets, background, width, height)
                        DrawFramesAnimation(cycle, frames, background, width, height)
                    }
                }
            }
        }
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

    @Composable
    private fun DrawRectAnimation(
        index: State<Float>,
        offsets: Array<Offset>,
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
                    drawRect(
                        color = Color.Green,
                        topLeft = offsets[index.value.toInt()],
                        size = Size(width.toPx(), width.toPx())
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
                    drawImage(frames[index.value.toInt()])
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
}