package dz.nexatech.tests.compose2sandbox

import android.content.res.Resources
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource

object PngFrames {

    fun loadFrames(resources: Resources) = Array(frames.size) {
        ImageBitmap.imageResource(resources, frames[it])
    }

    private val frames: Array<Int> = arrayOf(
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
}