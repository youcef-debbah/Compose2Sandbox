package dz.nexatech.tests.compose2sandbox

import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dz.nexatech.tests.compose2sandbox.ui.theme.Compose2SandboxTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
object Cache {
    val animatedImage: MutableState<Drawable?> = mutableStateOf(null)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= 28) {
            lifecycleScope.launch(Dispatchers.IO) {
                val source = ImageDecoder.createSource(resources, R.drawable.webp_anime)
                val drawable = ImageDecoder.decodeDrawable(source)
                withContext(Dispatchers.Main) {
                    Cache.animatedImage.value = drawable
                }
                if (drawable is AnimatedImageDrawable) {
                    delay(100)
                    drawable.start()
                }
            }
        }

        val offsets: Array<IntOffset> = Array(1401) {
            IntOffset(0, it)
        }

        val color = Color.Blue
        val width = 100.dp

        val animationSpec = infiniteRepeatable<Float>(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )

        setContent {
//            Compose2SandboxTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Column {
//                        Greeting(
//                            name = "Android",
//                            modifier = Modifier.padding(innerPadding)
//                        )
//                        Counter()
//                    }
//                }
//            }
            Row(
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .padding(40.dp)
                    .fillMaxSize()
            ) {
                OffsetAnimation(animationSpec, offsets, color, width)
                GifAnimation()
            }
        }
    }

    @Composable
    private fun OffsetAnimation(
        animationSpec: InfiniteRepeatableSpec<Float>,
        offsets: Array<IntOffset>,
        color: Color,
        width: Dp
    ) {
        val anime: InfiniteTransition = rememberInfiniteTransition(label = "anime")
        val offset: Float by anime.animateFloat(
            label = "offset_anime",
            initialValue = 0f,
            targetValue = 31.9999f,
            animationSpec = animationSpec,
        )
        Box(
            Modifier
                .offset { offsets[offset.toInt()] }
                .size(width)
                .background(color)
        )
    }

    @Composable
    private fun GifAnimation() {
        val painter = rememberDrawablePainter(Cache.animatedImage.value)
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Compose2SandboxTheme {
        Greeting("Android")
    }
}

@Composable
fun Counter() {
    var count by remember { mutableIntStateOf(0) }
    Btn(
        modifier = Modifier.padding(5.dp),
        onClick = { count += 1 }
    ) {
        Text("Count: $count", Modifier.hints())
    }
}

@Composable
fun Btn(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.filledTonalShape,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.filledTonalButtonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) = Button(
    onClick = onClick,
    modifier = modifier.hints(),
    enabled = enabled,
    shape = shape,
    colors = colors,
    elevation = elevation,
    border = border,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
    content = content
)