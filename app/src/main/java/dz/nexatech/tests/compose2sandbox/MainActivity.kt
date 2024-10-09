@file:OptIn(ExperimentalLayoutApi::class)

package dz.nexatech.tests.compose2sandbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.Text
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("Loading...")
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val allOffsets = calcPrecomputedViewGridOffsets(resources.displayMetrics.widthPixels)
            withContext(Dispatchers.Main) {
                reportFullyDrawn()
                setContent {
                    PrecomputedViewGrid(allOffsets)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        printFpsHistory()
    }
}